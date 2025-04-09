package cloudsim.ext.datacenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import cloudsim.ext.Constants;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.event.CloudSimEvents;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public class IntraBalancer_RL extends VmLoadBalancer implements CloudSimEventListener {

    // Enhanced state tracking
    private final Map<Integer, VMState> vmStates = new ConcurrentHashMap<>();
    private final AtomicInteger totalAllocations = new AtomicInteger(0);
    private final float[] vmWeights;
    private final Deque<Integer> recentSuccesses = new ArrayDeque<>(5);
    
    // Adaptive algorithm parameters
    private float explorationRate = 0.7f;
    private final float learningRate = 0.9f;
    private final float decayFactor = 0.995f;
    private final float loadSensitivity = 0.85f;

    public IntraBalancer_RL(DatacenterController dcb) {
        List<Integer> vmIds = new ArrayList<>(dcb.getVmStatesList().keySet());
        this.vmWeights = new float[vmIds.size()];
        Arrays.fill(vmWeights, 1.0f);
        
        vmIds.forEach(id -> vmStates.put(id, new VMState()));
        dcb.addCloudSimEventListener(this);
    }

    @Override
    public int getNextAvailableVm() {
        totalAllocations.incrementAndGet();
        updateExplorationRate();

        // Phase 1: Try recent success cache
        int cached = getCachedSelection();
        if (cached != -1) return cached;

        // Phase 2: RL-driven selection
        return makeWeightedSelection();
    }

    private int getCachedSelection() {
        synchronized (recentSuccesses) {
            Iterator<Integer> it = recentSuccesses.iterator();
            while (it.hasNext()) {
                int vmId = it.next();
                if (tryAllocate(vmId, true)) {
                    it.remove();
                    return vmId;
                }
            }
        }
        return -1;
    }

    private int makeWeightedSelection() {
        float totalWeight = 0;
        float[] weights = new float[vmWeights.length];
        
        // Calculate dynamic weights with load awareness
        for (int i = 0; i < vmWeights.length; i++) {
            VMState state = vmStates.get(getVmId(i));
            weights[i] = vmWeights[i] * (1 - state.loadFactor);
            totalWeight += weights[i];
        }

        // Stochastic selection
        float random = (float) (Math.random() * totalWeight);
        float cumulative = 0;
        
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (random <= cumulative) {
                int vmId = getVmId(i);
                if (tryAllocate(vmId, false)) {
                    updateModel(vmId, true);
                    return vmId;
                }
            }
        }
        
        // Fallback with penalty
        return handleFallback();
    }

    private boolean tryAllocate(int vmId, boolean fromCache) {
        VMState state = vmStates.get(vmId);
        if (state.available) {
            state.available = false;
            state.loadFactor = Math.min(1.0f, state.loadFactor + 0.1f);
            if (!fromCache) updateRecentSuccesses(vmId);
            return true;
        }
        state.loadFactor = Math.min(1.0f, state.loadFactor + 0.15f);
        return false;
    }

    private void updateRecentSuccesses(int vmId) {
        synchronized (recentSuccesses) {
            recentSuccesses.remove(vmId);
            recentSuccesses.addFirst(vmId);
            if (recentSuccesses.size() > 5) {
                recentSuccesses.removeLast();
            }
        }
    }

    private void updateModel(int vmId, boolean success) {
        float reward = success ? 1.5f : -2.0f;
        vmWeights[getVmIndex(vmId)] = Math.max(0.1f, 
            vmWeights[getVmIndex(vmId)] + learningRate * reward
        );
    }

    private int handleFallback() {
        // Linear scan with load prioritization
        return vmStates.entrySet().stream()
            .filter(e -> e.getValue().available)
            .min((a, b) -> Float.compare(a.getValue().loadFactor, b.getValue().loadFactor))
            .map(Map.Entry::getKey)
            .orElse(-1);
    }

    private void updateExplorationRate() {
        explorationRate = Math.max(0.05f, explorationRate * decayFactor);
    }

    @Override
    public void cloudSimEventFired(CloudSimEvent e) {
        if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET) {
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            VMState state = vmStates.get(vmId);
            state.available = true;
            state.loadFactor = Math.max(0.0f, state.loadFactor - 0.2f);
        }
    }

    // Diagnostic methods
    public float getExplorationRate() {
        return explorationRate;
    }
    
    public float[] getVmWeights() {
        return Arrays.copyOf(vmWeights, vmWeights.length);
    }

    private static class VMState {
        boolean available = true;
        float loadFactor = 0.0f;
    }

    // Utility methods
    private int getVmId(int index) {
        return new ArrayList<>(vmStates.keySet()).get(index);
    }
    
    private int getVmIndex(int vmId) {
        return new ArrayList<>(vmStates.keySet()).indexOf(vmId);
    }
}