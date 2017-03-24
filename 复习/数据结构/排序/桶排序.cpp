# include <iostream>

using namespace std;

/*
2017年3月19日19:46:05

桶排序Bucket sort

1,桶排序是稳定的

2,桶排序是常见排序里最快的一种,比快排还要快…大多数情况下

3,桶排序非常快,但是同时也非常耗空间,基本上是最耗空间的一种排序算法 
*/ 

int maxNumber = 95;

void bucket_sort(int a[],int n)
{
	int b[maxNumber] = {0};
	for(int i=0; i<n; i++)
	{
		b[a[i]] = a[i];
	}
	
	for(int i=0; i<maxNumber; i++)
	{
		if(b[i] > 0)
		{
			cout<<b[i]<<" ";
		} 
	}
} 

int main(void)
{
	int number[8] = {95, 45, 15, 78, 84, 51, 24, 12};
	bucket_sort(number,8);
	
	return 0;
}

