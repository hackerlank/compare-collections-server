package com.randioo.compare_collections_server.module.gm.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.module.gm.service.GmService;
import com.randioo.randioo_server_base.annotation.PTStringAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@PTStringAnnotation("gmDismiss")
@Component
public class GmDismissGameAction implements IActionSupport {

    @Autowired
    private GmService gmService;

    @Override
    public void execute(Object data, Object session) {
        String[] roomId = String.valueOf(data).split(" ");
        
    }

}
