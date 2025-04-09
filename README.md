## Introduction

This project addresses the challenge of uneven load distribution within datacenters in a federated cloud environment. This is the implementation of the [paper](https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=10629693), carried out using the CloudAnalyst simulation toolkit.

---
## Existing Results

The report presents two key test cases that compare the modified throttled algorithm (intra-balancer) against traditional algorithms such as Round-Robin and ESCE.

---
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

---
## Results

The adapted throttled algorithm (intra-balancer) demonstrated significant improvements:

- **Lower Latency:** Achieving an average response time as low as ~98 ms in high-resource scenarios.
- **Enhanced Processing Efficiency:** Processing times were reduced to as low as ~2 ms.
- **Scalability:** Increased number of VMs corresponded with better overall performance.

---
## Comparison and Analysis

A detailed comparison between the existing implementation and our version of the intra-balancer revealed:

- **Response Time:** Consistently lower for the intra-balancer compared to traditional methods.
- **Processing Time:** Minimized processing times due to efficient task scheduling.
- **Resource Utilization:** Dynamic VM status checks and request queuing improved resource allocation and reduced idle times.
