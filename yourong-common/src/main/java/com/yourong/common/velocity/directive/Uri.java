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

public class Uri extends AbstractInline {
    @Override
    public String getName() {
        return "uri";
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        if ( node.jjtGetNumChildren() == 0 )
        {
            throw new VelocityException("#require(): argument missing at " +
                    Log.formatFileString(this));
        }

        Resource fisResource = ResourceManager.getByContext(context);

        try {
            writer.write(fisResource.getUri(node.jjtGetChild(0).value(context).toString()));
        } catch (Exception err) {
            throw new VelocityException(err.getMessage() +
                    Log.formatFileString(this));
        }

//        ResourceManager.unRef(context);
        return true;
    }
}
