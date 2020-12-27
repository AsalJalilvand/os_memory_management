#### Final Project for Operating Systems course, Spring 2016, Shahid Beheshti University

In this project, a simple **memory management system** is implemented. 
* Four memory management algorithms are implemented: **LRU, FIFO, OPT, Second Chance**
* Memory is in form of Page Table and TLB structures.
* Per each request, if the page is in the main memory, the corresponding frame id is returned, else if required
a page is swapped out and replaced with a new one. In this case, the request must be repeated.

The first 5 lines of the input file is in form of:<br>
[number of frames in main memory]<br>
[space of current process in kb]<br>
[space per page]<br>
[algorithm name]<br>
[number of TLB rows]<br>

From line 6 and onwards, the requests in form of:<br>
[page number,offset,request]

The output will be a log with the following information:
* Update of PT or TLB
* Copy of a page from main memory to virtual and vice versa
* Change of modification of bit in case there is a change in a page
* Error in case a process wants to access an out-of-bound space
