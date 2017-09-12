# MVP基本认识

# data 

> 一般用于请求数据,或者处理数据

比如ApplicationFormRepository中有个getApplicationForm()接口方法;

那么在ApplicationFormRepositoryImpl需要实现该方法,并去请求网络,获取model数据.


# model

> 就是模型类,比如请求参数模型类,获取服务器返回的数据的模型类都行

# presenter

> 控制data和view,用data的示例去请求数据,用view进行显示

在这里面,有data的实现类的实例.和view的实体类

ChooseApplyContract(presenter的接口)->ChooseApplyPresenter(presenter实现)

ChooseApplyContract写一些接口方法

并在ChooseApplyContract中定义一个IView接口,

	interface IView extends BaseView {
        /**
         * 查询申请单结果回调
         * @param responseVo
         */
       void renderChooseAppForm(List<ApplicationFormRes> responseVo);
    }
	
这里将BaseView提取出来,BaseView也是一个接口,它是任何MVP的view的父接口,
它里面包含2个公用的接口,用来显示错误信息的.

	public interface BaseView {

	    void showErrorMsg(String msg);
	
	    void showErrorView(String msg);
	}

# view

> 这里的view一般就是Activity或者Fragment等等.里面有一个presenter实例,然后通过presenter去请求数据获取其他操作.

在view对象里面是只负责做界面的显示的,比如

- showErrorView();

- showLoading();

- showBookList();

view是需要实现presenter里面的IView接口的,方便通过presenter去调用.