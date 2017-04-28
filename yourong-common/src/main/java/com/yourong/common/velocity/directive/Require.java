package com.yourong.common.velocity.directive;


import com.yourong.common.velocity.util.Resource;
import com.yourong.common.velocity.util.ResourceManager;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

public class Require extends AbstractInline {


    @Override
    public String getName() {
        return "require";
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        String prefix = null;
        String affix = null;

        if ( node.jjtGetNumChildren() == 0 )
        {
            throw new VelocityException("#require(): argument missing at " +
                    Log.formatFileString(this));
        }

        if (node.jjtGetNumChildren() >1) {
            prefix = node.jjtGetChild(1).value(context).toString();
        }

        if (node.jjtGetNumChildren() >2) {
            affix = node.jjtGetChild(2).value(context).toString();
        }

        Resource fisResource = ResourceManager.getByContext(context);

        try {
            // 只需要把依赖加载上就可以了。
            fisResource.addResource(node.jjtGetChild(0).value(context).toString(), false, false, prefix, affix);
        } catch (Exception err) {
            writer.write(err.getMessage() + " " + Log.formatFileString(this));
            log.warn(err.getStackTrace());
        }

//        ResourceManager.unRef(context);

        return true;
    }
}
