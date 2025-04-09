## Introduction

This project addresses the challenge of uneven load distribution within datacenters in a federated cloud environment. This is the implementation of the [paper](https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=10629693), carried out using the CloudAnalyst simulation toolkit.

## Existing Results

The report presents two key test cases that compare the modified throttled algorithm (intra-balancer) against traditional algorithms such as Round-Robin and ESCE.

## Implementation

The implementation was developed using the CloudAnalyst simulation toolkit. Key steps included:

1. **Environment Setup**  
   - Installing Java SDKs and a Java IDE  
   - [Download](https://github.com/Ankit-IITJ/VCC_Project.git) and import the CloudAnalyst project
      
2. **Algorithm Adaptation**  
   - IntraBalancer.java is the implementation of the algorithm mentioned in the research paper.
     
3. **Simulation Execution**  
   - Running simulations by using different simulation settings
   - Recording performance metrics such as response and processing times
     
4. **Validation**  
   - Comparing the simulation results with Round-Robin and ESCE algorithms to verify improvements

5. **Plotting**
   - All results are plotted in ipynb file. 
