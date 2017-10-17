# include <iostream>

using namespace std;

/*
合并排序(归并排序)

2017年3月22日12:55:20

其的基本思路就是将数组分成二组A，B，如果这二组组内的数据都是有序的，
那么就可以很方便的将这二组数据进行排序。如何让这二组组内数据有序了？

可以将A，B组各自再分成二组。依次类推，当分出来的小组只有一个数据时，
可以认为这个小组组内已经达到了有序，然后再合并相邻的二个小组就可以了。
这样通过先递归的分解数列，再合并数列就完成了归并排序。
 
 归并排序的效率是比较高的，设数列长为N，将数列分开成小数列一共要logN步，
 每步都是一个合并有序数列的过程，时间复杂度可以记为O(N)，故一共为O(N*logN)。
 因为归并排序每次都是在相邻的数据中进行操作，
 所以归并排序在O(N*logN)的几种排序方法（快速排序，归并排序，希尔排序，堆排序）也是效率比较高的。
 
*/ 

//将有二个有序数列a[first...mid]和a[mid...last]合并。  
void mergearray(int a[], int first, int mid, int last, int temp[])  
{  
    int i = first, j = mid + 1;  
    int m = mid,   n = last;  
    int k = 0;  
      
    while (i <= m && j <= n)  
    {  
        if (a[i] <= a[j])  
            temp[k++] = a[i++];  
        else  
            temp[k++] = a[j++];  
    }  
      
    while (i <= m)  
        temp[k++] = a[i++];  
      
    while (j <= n)  
        temp[k++] = a[j++];  
      
    for (i = 0; i < k; i++)     //将结果放到a数组中 
        a[first + i] = temp[i];  
}  

//将数组递归地分成左右2份
//参数:待排序的数组 first-last  暂时存放数据的数组 
void mergesort(int a[], int first, int last, int temp[])  
{  
    if (first < last)  
    {  
        int mid = (first + last) / 2;  
        mergesort(a, first, mid, temp);    //左边有序  
        mergesort(a, mid + 1, last, temp); //右边有序  
        mergearray(a, first, mid, last, temp); //再将二个有序数列合并  
    }  
}  

//排序  数组a,长度n 
bool MergeSort(int a[], int n)  
{  
    int *p = new int[n];  
    if (p == NULL)  
        return false;  
    mergesort(a, 0, n - 1, p);  
    delete[] p;  
    return true;  
}  

int main(void)
{
	int a[] = {1,12,45,78,121,45,456465,45,134,53,435,12,456}; 
	MergeSort(a,13);
	for(int i=0; i<13; i++)
	{
		cout<<a[i]<<" ";
	}
    return 0;
}

