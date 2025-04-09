package cloudsim.ext.datacenter;

import java.util.Map;
import java.util.Iterator;

import cloudsim.ext.Constants;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.event.CloudSimEvents;
import cloudsim.ext.event.CloudSimEvent;

public class IntraBalancer extends VmLoadBalancer implements CloudSimEventListener {

    private Map<Integer, VirtualMachineState> vmStatesList;

    public IntraBalancer(DatacenterController dcb) {
        this.vmStatesList = dcb.getVmStatesList();
        dcb.addCloudSimEventListener(this);
    }

    
    @Override
    public int getNextAvailableVm() {
        int vmId = -1;

       
        Iterator<Integer> vmIterator = vmStatesList.keySet().iterator();

        
        while (vmIterator.hasNext()) {
            
            int tempVmId = vmIterator.next();

          
            VirtualMachineState state = vmStatesList.get(tempVmId);

          
            if (state == VirtualMachineState.AVAILABLE) {
                vmId = tempVmId;
                break;
            } else {
                
                continue; 
            }
        }

        
        allocatedVm(vmId); 
        return vmId; 
    }

   
    @Override
    public void cloudSimEventFired(CloudSimEvent e) {
        if (e.getId() == CloudSimEvents.EVENT_CLOUDLET_ALLOCATED_TO_VM) {
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            vmStatesList.put(vmId, VirtualMachineState.BUSY);
        } else if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET) {
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            vmStatesList.put(vmId, VirtualMachineState.AVAILABLE);
        }
    }
}