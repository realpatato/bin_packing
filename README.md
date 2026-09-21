# Bin Packing Algorithms

I work in the department that handles online orders at a store, and I noticed a lack of efficiency in the packing. 
It appeared to me that it may have been purely considering volume, and not the actual size of the objects in the bins. 
I wanted to improve this, or test some possible solutions. 

### The Language

The program is being created in Java because its the language used by the handheld devices used at many of the store that offer online ordering.
This way, it could hypothetically be integrated into already existing applications. 

### Tracking Data

I will log the data, tracking important metrics such as bins used, unused volume, and computation time.
While time complexity is important, the packing efficiency is more important.
The total number of items to pack is not a huge number, maxing out at around 200-300 for incredibly large orders.
This means the time, which is based on that number, will likely never get too large.
It should also be computed once, when the order is initially created, far before it reaches an employee.
