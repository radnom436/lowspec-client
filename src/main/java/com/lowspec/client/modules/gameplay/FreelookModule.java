package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;

public class FreelookModule extends Module {
    public FreelookModule() {
        super("freelook", "Freelook", "Separate camera rotation from body", Category.GAMEPLAY, ComplianceTag.SERVER_RESTRICTED, false);
    }
}