package today.opai.autojump;

import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;
import today.opai.autojump.modules.AutoJump;

@ExtensionInfo(name = "Auto Jump Extension", author = "Assistant", version = "1.0")
public class AutoJumpExtension extends Extension {
    public static OpenAPI openAPI;

    @Override
    public void initialize(OpenAPI openAPI) {
        AutoJumpExtension.openAPI = openAPI;
        
        // Register the AutoJump module
        openAPI.registerFeature(new AutoJump());
        
        openAPI.printMessage("§aAuto Jump Extension loaded successfully!");
    }
    
    @Override
    public void onUnload() {
        openAPI.printMessage("§cAuto Jump Extension unloaded.");
    }
}