# include <iostream>

using namespace std;

/*
2017年3月22日08:58:20

快速排序 
 不稳定排序
 快速排序（Quicksort）— O(nlogn) 期望时间,
  O(n2) 最坏情况; 对于大的、乱数串行一般相信是最快的已知排序.
  
  快排的思想

如下的三步用于描述快排的流程：

1.在数组中随机取一个值作为标兵
2.对标兵左、右的区间进行划分(将比标兵大的数放在标兵的右面，
比标兵小的数放在标兵的左面，如果倒序就反过来)
3.重复如上两个过程，直到选取了所有的标兵并划分(此时每个标兵决定的区间中只有一个值，
故有序)
*/ 

//快速排序的一趟循环
//算法：将一个数，比它小的放到该数的前面，比它大的放到该数的后面   
//这里的基准数是最传统的选法,直接将第一个作为基准数
int Partition(int a[],int low,int high)
{
	int pivotkey = a[low];  //基准数
	 while(low<high)
	 {
	 	while(low<high && a[high]>=pivotkey)
	 	{
	 		high--;
		 }
		 a[low] = a[high];  //如果遇到比基准数小的,则将其放到a[low]位置 
		 while(low<high && a[low]<=pivotkey)
		 {
		 	low++;
		 }
		 a[high] = a[low];  //如果遇到比基准数大的,则将其放到a[high]位置 
	 } 
	 a[low] =  pivotkey;   //将基准数放到该放的位置 
	 return low;   //返回这一次已经放好位置了的基准数的位置 
} 

//数组快速排序
void Sort(int a[],int low,int high)
{
	if(low<high)
	{
		int pivotloc = Partition(a,low,high);    //基准数的位置 
		Sort(a,low,pivotloc-1);   //分治法 
		Sort(a,pivotloc+1,high);
	}
} 

int main(void)
{
	int a[] = {4,45,12,6,45,78,124,52,15,45,12};
	
	Sort(a,0,10);
	
	//输出数组
	for(int i=0; i<11; i++)
	{
		cout<<a[i]<<" ";
	}

	cout<<endl<<" ";
   return 0;
}

