> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 http://www.importnew.com/23035.html 原文出处： [朱吉芳（@攻城师 - 翡青 ）](http://blog.csdn.net/zjf280441589/article/details/53946312)

> JVM 内存的分配与回收大致可分为如下 4 个步骤: 何时分配 -> 怎样分配 -> 何时回收 -> 怎样回收.
> 除了在概念上可简单认为`new`时分配外, 我们着重介绍后面的 3 个步骤:

![image](7B0EA4C3AA91488CA4F2B683E3F1BE67)

* * *

## <a></a>I. 怎样分配 - JVM 内存分配策略

对象内存主要分配在新生代 **Eden 区**, 如果启用了本地线程分配缓冲, 则**优先在 TLAB 上分配**, 少数情况能会直接分配在老年代, 或被拆分成标量类型在栈上分配 (JIT 优化). 分配的规则并不是百分百固定, 细节主要取决于垃圾收集器组合, 以及 VM 内存相关的参数.

* * *

### <a></a>对象分配

*   优先在 Eden 区分配
    在 [JVM 内存模型](http://blog.csdn.net/zjf280441589/article/details/53437703)一文中, 我们大致了解了 VM 年轻代堆内存可以划分为一块 Eden 区和两块 Survivor 区. 在大多数情况下, 对象在新生代 Eden 区中分配, 当 Eden 区没有足够空间分配时, VM 发起一次 Minor GC, 将 Eden 区和其中一块 Survivor 区内尚存活的对象放入另一块 Survivor 区域, 如果在 Minor GC 期间发现新生代存活对象无法放入空闲的 Survivor 区, 则会通过**空间分配担保机制**使对象提前进入老年代 (空间分配担保见下).
*   大对象直接进入老年代
    Serial 和 ParNew 两款收集器提供了 **-XX:PretenureSizeThreshold** 的参数, 令大于该值的大对象直接在老年代分配, 这样做的目的是**避免在 Eden 区和 Survivor 区之间产生大量的内存复制** (大对象一般指 _需要大量连续内存的 Java 对象_, 如很长的字符串和数组), 因此大对象容易导致**_还有不少空闲内存就提前触发 GC 以获取足够的连续空间_**.

* * *

### <a></a>对象晋升

*   年龄阈值
    VM 为每个对象定义了一个对象年龄 (Age) 计数器, 对象在 Eden 出生如果**经第一次 Minor GC 后仍然存活, 且能被 Survivor 容纳的话**, 将被移动到 Survivor 空间中, 并将**年龄设为 1**. 以后**对象在 Survivor 区中每熬过一次 Minor GC 年龄就 + 1**. 当增加到一定程度 (**-XX:MaxTenuringThreshold**, 默认 15), 将会晋升到老年代.
*   提前晋升: 动态年龄判定
    然而 VM 并不总是要求对象的年龄必须达到 **MaxTenuringThreshold** 才能晋升老年代: **_如果在 Survivor 空间中相同年龄所有对象大小的总和大于 Survivor 空间的一半, 年龄大于或等于该年龄的对象就可以直接进入老年代_**, 而无须等到晋升年龄.

* * *

## <a></a>II. 何时回收 - 对象生死判定

(哪些内存需要回收 / 何时回收)

> 在堆里面存放着 Java 世界中几乎所有的对象实例, 垃圾收集器在对堆进行回收前, 第一件事就是判断哪些对象**已死** (可回收).

* * *

### <a></a>可达性分析算法

在主流商用语言 (如 Java、C#) 的主流实现中, 都是通过**可达性分析算法**来判定对象是否存活的: 通过一系列的称为 **GC Roots** 的对象作为起点, 然后向下搜索; 搜索所走过的路径称为**引用链 / Reference Chain**, 当一个对象到 **GC Roots** 没有任何**引用链**相连时, 即该对象不可达, 也就说明此对象是不可用的, 如下图: _Object5、6、7_ 虽然互有关联, 但它们到 GC Roots 是不可达的, 因此也会被判定为可回收的对象:
![](http://7xrgh9.com1.z0.glb.clouddn.com/16-11-8/4240985.jpg)

*   在 Java, 可作为 **GC Roots** 的对象包括:
    1.  方法区: 类静态属性引用的对象;
    2.  方法区: 常量引用的对象;
    3.  虚拟机栈 (本地变量表) 中引用的对象.
    4.  本地方法栈 JNI(Native 方法) 中引用的对象。

> 注: 即使在**可达性分析算法**中不可达的对象, VM 也并不是马上对其回收, 因为要真正宣告一个对象死亡, 至少要经历两次标记过程: 第一次是在可达性分析后发现没有与 GC Roots 相连接的引用链, 第二次是 GC 对在 **F-Queue** 执行队列中的对象进行的小规模标记 (对象需要覆盖`finalize()`方法且没被调用过).

* * *

## <a></a>III. GC 原理 - 垃圾收集算法

### <a></a>分代收集算法 VS 分区收集算法

*   分代收集
    当前主流 VM 垃圾收集都采用” 分代收集”(Generational Collection) 算法, 这种算法会根据对象存活周期的不同将内存划分为几块, 如 JVM 中的 **新生代**、**老年代**、**永久代**. 这样就可以根据各年代特点分别采用最适当的 GC 算法:
    *   在新生代: 每次垃圾收集都能发现大批对象已死, 只有少量存活. 因此选用复制算法, **_只需要付出少量存活对象的复制成本就可以完成收集_**.
    *   在老年代: 因为对象存活率高、没有额外空间对它进行分配担保, 就必须采用 **“标记—清理”** 或 **“标记—整理”** 算法来进行回收, **_不必进行内存复制, 且直接腾出空闲内存_**.
*   分区收集
    上面介绍的分代收集算法是将对象的生命周期按长短划分为两个部分, 而分区算法则将整个堆空间划分为连续的不同小区间, 每个小区间独立使用, 独立回收. 这样做的好处是**可以控制一次回收多少个小区间**.
    在相同条件下, 堆空间越大, 一次 GC 耗时就越长, 从而产生的停顿也越长. 为了更好地控制 GC 产生的停顿时间, 将一块大的内存区域分割为多个小块, 根据目标停顿时间, 每次合理地回收若干个小区间 (而不是整个堆), 从而减少一次 GC 所产生的停顿.

* * *

### <a></a>分代收集

#### <a></a>新生代 - 复制算法

该算法的核心是**将可用内存按容量划分为大小相等的两块, 每次只用其中一块, 当这一块的内存用完, 就将还存活的对象复制到另外一块上面, 然后把已使用过的内存空间一次清理掉**.
![](https://si.geilicdn.com/hz_img_020100000158fcd79a330a026860_577_382.jpeg)
(图片来源: [jvm 垃圾收集算法](https://my.oschina.net/winHerson/blog/114391))

这使得每次只对其中一块内存进行回收, 分配也就不用考虑内存碎片等复杂情况, 实现简单且运行高效.
![](https://si.geilicdn.com/hz_img_00fb00000158fcd708b90a02685e_625_308.jpeg)
现代商用 VM 的新生代均采用复制算法, 但由于新生代中的 98% 的对象都是生存周期极短的, 因此并不需完全按照 1∶1 的比例划分新生代空间, 而是**将新生代划分为一块较大的 Eden 区和两块较小的 Survivor 区** (HotSpot 默认 Eden 和 Survivor 的大小比例为 8∶1), 每次只用 Eden 和其中一块 Survivor. 当发生 MinorGC 时, 将 Eden 和 Survivor 中还存活着的对象一次性地拷贝到另外一块 Survivor 上, 最后清理掉 Eden 和刚才用过的 Survivor 的空间. 当 Survivor 空间不够用 (不足以保存尚存活的对象) 时, 需要依赖老年代进行空间分配担保机制, 这部分内存直接进入老年代.

* * *

#### <a></a>老年代 - 标记清除算法

该算法分为 “标记” 和“清除”两个阶段: _首先标记出所有需要回收的对象 (可达性分析), 在标记完成后统一清理掉所有被标记的对象_.
![](https://si.geilicdn.com/hz_img_00fd00000158fcdb75e40a02685e_574_435.jpeg)
该算法会有以下两个问题:
1\. 效率问题: 标记和清除过程的效率都不高;
2\. 空间问题: 标记清除后会产生大量不连续的内存碎片, 空间碎片太多可能会导致在运行过程中需要分配较大对象时无法找到足够的连续内存而不得不提前触发另一次垃圾收集.
![](https://si.geilicdn.com/hz_img_00fe00000158fcddd01d0a02685e_625_425.jpeg)

* * *

#### <a></a>老年代 - 标记整理算法

标记清除算法会产生内存碎片问题, 而复制算法需要有额外的内存担保空间, 于是针对老年代的特点, 又有了**标记整理算法**. 标记整理算法的标记过程与标记清除算法相同, 但后续步骤不再对可回收对象直接清理, 而是让所有存活的对象都向一端移动, 然后清理掉端边界以外的内存.
![](https://si.geilicdn.com/hz_img_020200000158fce1f2060a026860_574_436.jpeg)

* * *

#### <a></a>永久代 - 方法区回收

*   在方法区进行垃圾回收一般” 性价比” 较低, 因为在方法区主要回收两部分内容: **废弃常量**和**无用的类**. 回收废弃常量与回收其他年代中的对象类似, 但要判断一个类是否无用则条件相当苛刻:
    1.  该类所有的实例都已经被回收, Java 堆中不存在该类的任何实例;
    2.  该类对应的`Class`对象没有在任何地方被引用 (也就是在任何地方都无法通过反射访问该类的方法);
    3.  加载该类的 **ClassLoader** 已经被回收.

但即使满足以上条件也未必一定会回收, Hotspot VM 还提供了 **-Xnoclassgc** 参数控制 (关闭 CLASS 的垃圾回收功能). 因此在大量使用动态代理、CGLib 等字节码框架的应用中一定要关闭该选项, 开启 VM 的类卸载功能, 以保证方法区不会溢出.

* * *

### <a></a>补充: 空间分配担保

在执行 Minor GC 前, VM 会首先检查老年代是否有足够的空间存放新生代尚存活对象, 由于新生代使用**复制收集算法**, 为了提升内存利用率, 只使用了其中一个 **Survivor** 作为轮换备份, 因此当出现大量对象在 Minor GC 后仍然存活的情况时, 就需要老年代进行分配担保, 让 Survivor 无法容纳的对象直接进入老年代, 但前提是老年代需要有足够的空间容纳这些存活对象. 但存活对象的大小在实际完成 GC 前是无法明确知道的, 因此 Minor GC 前, VM 会先**首先检查老年代连续空间是否大于新生代对象总大小或历次晋升的平均大小**, 如果条件成立, 则进行 Minor GC, 否则进行 Full GC(让老年代腾出更多空间).
然而取历次晋升的对象的平均大小也是有一定风险的, 如果某次 Minor GC 存活后的对象突增, 远远高于平均值的话, 依然可能导致担保失败 (Handle Promotion Failure, 老年代也无法存放这些对象了), 此时就只好在失败后重新发起一次 Full GC(让老年代腾出更多空间).

* * *

## <a></a>IX. GC 实现 - 垃圾收集器

![](http://7xrgh9.com1.z0.glb.clouddn.com/16-11-7/13119604.jpg)

> GC 实现目标: 准确、高效、低停顿、空闲内存规整.

* * *

### <a></a>新生代

#### <a></a>1\. Serial 收集器

Serial 收集器是 Hotspot 运行在 Client 模式下的**默认新生代收集器**, 它的特点是 **只用一个 CPU / 一条收集线程去完成 GC 工作, 且在进行垃圾收集时必须暂停其他所有的工作线程 (“Stop The World” - 后面简称 STW)**.
![](http://7xrgh9.com1.z0.glb.clouddn.com/16-12-25/88879597-file_1482645155971_5a8f.png)
虽然是单线程收集, 但它却简单而高效, 在 VM 管理内存不大的情况下 (收集几十 M~ 一两百 M 的新生代), 停顿时间完全可以控制在几十毫秒~ 一百多毫秒内.

* * *

#### <a></a>2\. ParNew 收集器

ParNew 收集器其实是前面 **Serial 的多线程版本**, 除**使用多条线程进行 GC** 外, 包括 Serial 可用的所有控制参数、收集算法、STW、对象分配规则、回收策略等都与 Serial 完全一样 (也是 VM 启用 CMS 收集器`-XX: +UseConcMarkSweepGC`的默认新生代收集器).
![](http://7xrgh9.com1.z0.glb.clouddn.com/16-12-25/42481005-file_1482645395947_eae8.png)
由于存在线程切换的开销, ParNew 在单 CPU 的环境中比不上 Serial, 且在通过超线程技术实现的两个 CPU 的环境中也不能 100% 保证能超越 Serial. 但随着可用的 CPU 数量的增加, 收集效率肯定也会大大增加 (ParNew 收集线程数与 CPU 的数量相同, 因此在 CPU 数量过大的环境中, 可用`-XX:ParallelGCThreads`参数控制 GC 线程数).

* * *

#### <a></a>3\. Parallel Scavenge 收集器

与 ParNew 类似, Parallel Scavenge 也是**使用复制算法**, 也是**并行多线程收集器**. 但与其他收集器关注_尽可能缩短垃圾收集时间_不同, Parallel Scavenge 更关注**系统吞吐量**:

系统吞吐量 = 运行用户代码时间 (运行用户代码时间 + 垃圾收集时间)

停顿时间越短就越适用于用户交互的程序 - 良好的响应速度能提升用户的体验; 而高吞吐量则适用于后台运算而不需要太多交互的任务 - 可以最高效率地利用 CPU 时间, 尽快地完成程序的运算任务. Parallel Scavenge 提供了如下参数设置系统吞吐量:

| Parallel Scavenge 参数 | 描述 |
| :-: | :-- |
| `MaxGCPauseMillis` | (毫秒数) 收集器将尽力保证内存回收花费的时间不超过设定值, 但如果太小将会导致 GC 的频率增加. |
| `GCTimeRatio` | (整数:`0 < GCTimeRatio < 100`) 是垃圾收集时间占总时间的比率 |
| `-XX:+UseAdaptiveSizePolicy` | 启用 GC 自适应的调节策略: 不再需要手工指定`-Xmn`、`-XX:SurvivorRatio`、`-XX:PretenureSizeThreshold`等细节参数, VM 会根据当前系统的运行情况收集性能监控信息, 动态调整这些参数以提供最合适的停顿时间或最大的吞吐量 |

* * *

### <a></a>老年代

#### <a></a>Serial Old 收集器

Serial Old 是 Serial 收集器的老年代版本, 同样是**单线程收集器**, 使用 **“标记 - 整理” 算法**:
![](http://7xrgh9.com1.z0.glb.clouddn.com/16-12-25/14861810-file_1482646075655_14099.png)

*   Serial Old 应用场景如下:
    *   JDK 1.5 之前与 Parallel Scavenge 收集器搭配使用;
    *   作为 CMS 收集器的后备预案, 在并发收集发生`Concurrent Mode Failure`时启用 (见下: CMS 收集器).

* * *

#### <a></a>Parallel Old 收集器

Parallel Old 是 Parallel Scavenge 收老年代版本, 使用**多线程和 “标记－整理” 算法, 吞吐量优先**, 主要与 Parallel Scavenge 配合在 _注重吞吐量_ 及 _CPU 资源敏感_ 系统内使用:
![](http://7xrgh9.com1.z0.glb.clouddn.com/16-12-25/33750037-file_1482646585286_4804.png)

* * *

#### <a></a>CMS 收集器

CMS(Concurrent Mark Sweep) 收集器是一款具有划时代意义的收集器, 一款真正意义上的并发收集器, 虽然现在已经有了理论意义上表现更好的 G1 收集器, 但现在主流互联网企业线上选用的仍是 CMS(如 Taobao、微店).
CMS 是一种以**获取最短回收停顿时间为目标的收集器** (CMS 又称_多并发低暂停的收集器_), 基于” 标记 - 清除” 算法实现, 整个 GC 过程分为以下 4 个步骤:
1. **初始标记** (CMS initial mark)
2\. 并发标记 (CMS concurrent mark: GC Roots Tracing 过程)
3. **重新标记** (CMS remark)
4\. 并发清除 (CMS concurrent sweep: 已死象将会就地释放, 注意: _此处没有压缩_)
其中两个加粗的步骤 (**初始标记**、**重新标记**) 仍需 STW. 但初始标记仅只标记一下 GC Roots 能直接关联到的对象, 速度很快; 而重新标记则是为了修正并发标记期间因用户程序继续运行而导致标记产生变动的那一部分对象的标记记录, 虽然一般比初始标记阶段稍长, 但要远小于并发标记时间.
![](http://7xrgh9.com1.z0.glb.clouddn.com/16-12-25/60535235-file_1482651924054_16659.png)
(由于整个 GC 过程**耗时最长的并发标记和并发清除阶段的 GC 线程可与用户线程一起工作**, 所以总体上 CMS 的 GC 过程是与用户线程一起并发地执行的.

由于 CMS 收集器将整个 GC 过程进行了更细粒度的划分, 因此可以实现**并发收集、低停顿**的优势, 但它也并非十分完美, 其存在缺点及解决策略如下:

1.  CMS 默认启动的回收线程数 =(CPU 数目 + 3)4

    当 CPU 数 > 4 时, GC 线程最多占用不超过`25%`的 CPU 资源, 但是当 CPU 数 <=4 时, GC 线程可能就会过多的占用用户 CPU 资源, 从而导致应用程序变慢, 总吞吐量降低.

2.  无法处理浮动垃圾, 可能出现 _Promotion Failure_、_Concurrent Mode Failure_ 而导致另一次 Full GC 的产生: 浮动垃圾是指在 CMS 并发清理阶段用户线程运行而产生的新垃圾. 由于在 GC 阶段用户线程还需运行, 因此还需要预留足够的内存空间给用户线程使用, 导致 CMS 不能像其他收集器那样等到老年代几乎填满了再进行收集. 因此 CMS 提供了`-XX:CMSInitiatingOccupancyFraction`参数来设置 GC 的触发百分比 (以及`-XX:+UseCMSInitiatingOccupancyOnly`来启用该触发百分比), 当老年代的使用空间超过该比例后 CMS 就会被触发 (JDK 1.6 之后默认 92%). 但当 CMS 运行期间预留的内存无法满足程序需要, 就会出现上述 _Promotion Failure_ 等失败, 这时 VM 将启动后备预案: 临时启用 Serial Old 收集器来重新执行 Full GC(CMS 通常配合大内存使用, 一旦大内存转入串行的 Serial GC, 那停顿的时间就是大家都不愿看到的了).
3.  最后, 由于 CMS 采用” 标记 - 清除” 算法实现, 可能会产生大量内存碎片. 内存碎片过多可能会导致无法分配大对象而提前触发 Full GC. 因此 CMS 提供了`-XX:+UseCMSCompactAtFullCollection`开关参数, 用于在 Full GC 后再执行一个碎片整理过程. 但内存整理是无法并发的, 内存碎片问题虽然没有了, 但停顿时间也因此变长了, 因此 CMS 还提供了另外一个参数`-XX:CMSFullGCsBeforeCompaction`用于设置在执行 N 次不进行内存整理的 Full GC 后, 跟着来一次带整理的 (默认为 0: 每次进入 Full GC 时都进行碎片整理).

* * *

### <a></a>分区收集 - G1 收集器

> G1(Garbage-First) 是一款面向服务端应用的收集器, 主要目标用于配备多颗 CPU 的服务器治理大内存.
> - G1 is planned as the long term replacement for the Concurrent Mark-Sweep Collector (CMS).
> - `-XX:+UseG1GC` 启用 G1 收集器.

与其他基于分代的收集器不同, G1 将整个 Java 堆划分为多个大小相等的独立区域 (Region), 虽然还保留有新生代和老年代的概念, 但新生代和老年代不再是物理隔离的了, 它们都是一部分 Region(不需要连续) 的集合.
![](https://si.geilicdn.com/hz_img_09170000015911c3fa560a02685e_784_405_unadjust.png)
每块区域既有可能属于 O 区、也有可能是 Y 区, 因此不需要一次就对整个老年代 / 新生代回收. 而是**当线程并发寻找可回收的对象时, 有些区块包含可回收的对象要比其他区块多很多. 虽然在清理这些区块时 G1 仍然需要暂停应用线程, 但可以用相对较少的时间优先回收垃圾较多的 Region**(这也是 G1 命名的来源). 这种方式保证了 G1 可以在有限的时间内获取尽可能高的收集效率.

* * *

#### <a></a>新生代收集

![](https://si.geilicdn.com/hz_img_0fc40000015911cda63b0a026860_603_319_unadjust.png)

G1 的新生代收集跟 ParNew 类似: 存活的对象被转移到一个 / 多个 **Survivor Regions**. 如果存活时间达到阀值, 这部分对象就会被提升到老年代.
![](https://si.geilicdn.com/hz_img_091d0000015911e2fcb30a02685e_746_397_unadjust.png)

*   G1 的新生代收集特点如下:
    *   一整块堆内存被分为多个 Regions.
    *   存活对象被拷贝到新的 Survivor 区或老年代.
    *   年轻代内存由一组不连续的 heap 区组成, 这种方法使得可以动态调整各代区域尺寸.
    *   Young GCs 会有 STW 事件, 进行时所有应用程序线程都会被暂停.
    *   多线程并发 GC.

* * *

#### <a></a>老年代收集

G1 老年代 GC 会执行以下阶段:

> 注: 一下有些阶段也是年轻代垃圾收集的一部分.

| index | Phase | Description |
| :-: | :-: | :-- |
| (1) | 初始标记 (Initial Mark: Stop the World Event) | 在 G1 中, 该操作附着一次年轻代 GC, 以标记 Survivor 中有可能引用到老年代对象的 Regions. |
| (2) | 扫描根区域 (Root Region Scanning: 与应用程序并发执行) | 扫描 Survivor 中能够引用到老年代的 references. 但必须在 Minor GC 触发前执行完. |
| (3) | 并发标记 (Concurrent Marking : 与应用程序并发执行) | 在整个堆中查找存活对象, 但该阶段可能会被 Minor GC 中断. |
| (4) | 重新标记 (Remark : Stop the World Event) | 完成堆内存中存活对象的标记. 使用 **snapshot-at-the-beginning(SATB, 起始快照)** 算法, 比 CMS 所用算法要快得多 (空 Region 直接被移除并回收, 并计算所有区域的活跃度). |
| (5) | 清理 (Cleanup : Stop the World Event and Concurrent) | 见下 5-1、2、3 |
|  | 5-1 (Stop the world) | 在含有存活对象和完全空闲的区域上进行统计 |
|  | 5-2 (Stop the world) | 擦除 Remembered Sets. |
|  | 5-3 (Concurrent) | 重置空 regions 并将他们返还给空闲列表 (free list) |
| (*) | Copying/Cleanup (Stop the World Event) | 选择” 活跃度” 最低的区域 (这些区域可以最快的完成回收). 拷贝 / 转移存活的对象到新的尚未使用的 regions. 该阶段会被记录在 gc-log 内 (只发生年轻代`[GC pause (young)]`, 与老年代一起执行则被记录为`[GC Pause (mixed)]`. |

> 详细步骤可参考 [Oracle 官方文档 - The G1 Garbage Collector Step by Step](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/G1GettingStarted/index.html#t5).

*   G1 老年代 GC 特点如下:
    *   并发标记阶段 (index 3)
        1.  在与应用程序并发执行的过程中会计算活跃度信息.
        2.  这些活跃度信息标识出那些 regions 最适合在 STW 期间回收 (_which regions will be best to reclaim during an evacuation pause_).
        3.  不像 CMS 有清理阶段.
    *   再次标记阶段 (index 4)
        1.  使用 Snapshot-at-the-Beginning(SATB) 算法比 CMS 快得多.
        2.  空 region 直接被回收.
    *   拷贝 / 清理阶段 (Copying/Cleanup Phase)
        *   年轻代与老年代同时回收.
        *   老年代内存回收会基于他的活跃度信息.

* * *

#### <a></a>补充: 关于 Remembered Set

G1 收集器中, Region 之间的对象引用以及其他收集器中的新生代和老年代之间的对象引用都是**使用 Remembered Set 来避免扫描全堆**. G1 中每个 Region 都有一个与之对应的 Remembered Set, VM 发现程序对 Reference 类型数据进行写操作时, 会产生一个 Write Barrier 暂时中断写操作, 检查 Reference 引用的对象是否处于不同的 Region 中 (在分代例子中就是检查是否老年代中的对象引用了新生代的对象), 如果是, 便通过 CardTable 把相关引用信息记录到被引用对象所属的 Region 的 Remembered Set 中. 当内存回收时, 在 GC 根节点的枚举范围加入 Remembered Set 即可保证不对全局堆扫描也不会有遗漏.

* * *

## <a></a>V. JVM 小工具

在 ${JAVA_HOME}/bin / 目录下 Sun/Oracle 给我们提供了一些处理应用程序性能问题、定位故障的工具, 包含

| bin | 描述 | 功能 |
| :-: | :-- | :-- |
| jps | 打印 Hotspot VM 进程 | VMID、JVM 参数、`main()`函数参数、主类名 / Jar 路径 |
| jstat | 查看 Hotspot VM **运行时**信息 | 类加载、内存、GC[**可分代查看**]、JIT 编译 |
| jinfo | 查看和修改虚拟机各项配置 | `-flag name=value` |
| jmap | heapdump: 生成 VM 堆转储快照、查询 finalize 执行队列、Java 堆和永久代详细信息 | `jmap -dump:live,format=b,file=heap.bin [VMID]` |
| jstack | 查看 VM 当前时刻的线程快照: 当前 VM 内每一条线程正在执行的方法堆栈集合 | `Thread.getAllStackTraces()`提供了类似的功能 |
| javap | 查看经 javac 之后产生的 JVM 字节码代码 | 自动解析`.class`文件, 避免了去理解 class 文件格式以及手动解析 class 文件内容 |
| jcmd | 一个多功能工具, 可以用来导出堆, 查看 Java 进程、导出线程信息、 执行 GC、查看性能相关数据等 | 几乎集合了 jps、jstat、jinfo、jmap、jstack 所有功能 |
| **_jconsole_** | 基于 JMX 的可视化监视、管理工具 | 可以查看内存、线程、类、CPU 信息, 以及对 JMX MBean 进行管理 |
| **_jvisualvm_** | JDK 中最强大运行监视和故障处理工具 | 可以监控内存泄露、跟踪垃圾回收、执行时内存分析、CPU 分析、线程分析… |

* * *

## <a></a>VI. VM 常用参数整理

| 参数 | 描述 |
| :-- | :-- |
| `-Xms` | 最小堆大小 |
| `-Xmx` | 最大堆大小 |
| `-Xmn` | 新生代大小 |
| `-XX:PermSize` | 永久代大小 |
| `-XX:MaxPermSize` | 永久代最大大小 |
| `-XX:+PrintGC` | 输出 GC 日志 |
| `-verbose:gc` | - |
| `-XX:+PrintGCDetails` | 输出 GC 的详细日志 |
| `-XX:+PrintGCTimeStamps` | 输出 GC 时间戳 (以基准时间的形式) |
| `-XX:+PrintHeapAtGC` | 在进行 GC 的前后打印出堆的信息 |
| `-Xloggc:/path/gc.log` | 日志文件的输出路径 |
| `-XX:+PrintGCApplicationStoppedTime` | 打印由 GC 产生的停顿时间 |

> 在此处无法列举所有的参数以及他们的应用场景, 详细移步 [Oracle 官方文档 - Java HotSpot VM Options](http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html).

* * *

<dl>

<dt>参考 & 扩展</dt>

<dd>[深入理解 Java 虚拟机](https://book.douban.com/subject/24722612/)</dd>

<dd>[JVM 内幕：Java 虚拟机详解](http://www.importnew.com/17770.html) (力荐)</dd>

<dd>[JVM 中的 G1 垃圾回收器](http://www.importnew.com/15311.html)</dd>

<dd>[G1 垃圾收集器入门](http://blog.csdn.net/renfufei/article/details/41897113)</dd>

<dd>[Getting Started with the G1 Garbage Collector](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/G1GettingStarted/index.html)</dd>

<dd>[深入理解 G1 垃圾收集器](http://ifeve.com/%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3g1%E5%9E%83%E5%9C%BE%E6%94%B6%E9%9B%86%E5%99%A8/)</dd>

<dd>[解析 JDK 7 的 Garbage-First 收集器](http://www.infoq.com/cn/articles/jdk7-garbage-first-collector)</dd>

<dd>[The Garbage-First Garbage Collector](http://www.oracle.com/technetwork/java/javase/tech/g1-intro-jsp-135488.html)</dd>

<dd>[Memory Management in the Java HotSpot Virtual Machine](http://www.oracle.com/technetwork/java/javase/memorymanagement-whitepaper-150215.pdf)</dd>

<dd>[Java HotSpot VM Options](http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html)</dd>

<dd>[JVM 实用参数（一）JVM 类型以及编译器模式](http://ifeve.com/useful-jvm-flags-part-1-jvm-types-and-compiler-modes-2/)</dd>

<dd>[JVM 内存回收理论与实现](http://www.infoq.com/cn/articles/jvm-memory-collection)</dd>

<dd>[基于 OpenJDK 深度定制的淘宝 JVM（TaobaoVM）](http://book.51cto.com/art/201504/472732.htm)</dd>

</dl>