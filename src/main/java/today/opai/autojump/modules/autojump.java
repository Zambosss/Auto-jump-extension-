package today.opai.autojump.modules;

import today.opai.api.enums.EnumKeybind;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.modules.values.BooleanValue;
import today.opai.api.interfaces.modules.values.NumberValue;
import today.opai.autojump.AutoJumpExtension;

public class AutoJump extends ExtensionModule implements EventHandler {
    
    // Module settings
    private BooleanValue onlyWhileMoving;
    private BooleanValue requireForward;
    private NumberValue jumpDelay;
    
    // Internal state
    private long lastJumpTime = 0;
    
    public AutoJump() {
        super("Auto Jump", "Automatically jumps when on ground and moving forward", EnumModuleCategory.MOVEMENT);
        setEventHandler(this);
        initializeValues();
    }
    
    private void initializeValues() {
        // Create configuration values
        onlyWhileMoving = AutoJumpExtension.openAPI.getValueManager().createBoolean("Only While Moving", true);
        requireForward = AutoJumpExtension.openAPI.getValueManager().createBoolean("Require Forward Key", true);
        jumpDelay = AutoJumpExtension.openAPI.getValueManager().createDouble("Jump Delay", 0.0, 0.0, 1000.0, 50.0);
        jumpDelay.setSuffix("ms");
        
        // Add values to the module
        addValues(onlyWhileMoving, requireForward, jumpDelay);
    }
    
    @Override
    public void onPlayerUpdate() {
        if (!isEnabled()) return;
        
        try {
            // Get current time for delay checking
            long currentTime = System.currentTimeMillis();
            
            // Check if enough time has passed since last jump
            if (currentTime - lastJumpTime < jumpDelay.getValue()) {
                return;
            }
            
            // Check if player is on ground
            if (!AutoJumpExtension.openAPI.getLocalPlayer().isOnGround()) {
                return;
            }
            
            // Check if forward key is pressed (if required)
            if (requireForward.getValue() && !AutoJumpExtension.openAPI.getOptions().isPressed(EnumKeybind.FORWARD)) {
                return;
            }
            
            // Check if player is actually moving (if required)
            if (onlyWhileMoving.getValue()) {
                double motionX = AutoJumpExtension.openAPI.getLocalPlayer().getMotion().getX();
                double motionZ = AutoJumpExtension.openAPI.getLocalPlayer().getMotion().getZ();
                double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
                
                // If speed is too low, don't jump
                if (speed < 0.01) {
                    return;
                }
            }
            
            // All conditions met - jump!
            AutoJumpExtension.openAPI.getLocalPlayer().jump();
            lastJumpTime = currentTime;
            
        } catch (Exception e) {
            // Safety catch to prevent crashes
            AutoJumpExtension.openAPI.printMessage("§cAuto Jump error: " + e.getMessage());
        }
    }
    
    @Override
    public void onEnabled() {
        super.onEnabled();
        AutoJumpExtension.openAPI.printMessage("§aAuto Jump enabled!");
        lastJumpTime = 0; // Reset jump timer when enabled
    }
    
    @Override
    public void onDisabled() {
        super.onDisabled();
        AutoJumpExtension.openAPI.printMessage("§cAuto Jump disabled!");
    }
    
    @Override
    public String getSuffix() {
        // Show current delay in module suffix
        if (jumpDelay.getValue() > 0) {
            return jumpDelay.getValue().intValue() + "ms";
        }
        return null;
    }
}