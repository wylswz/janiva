package com.oracle.truffle.jx.nodes.core;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.impl.FrameWithoutBoxing;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.jx.JSONXLang;
import com.oracle.truffle.jx.nodes.JXExpressionNode;
import com.oracle.truffle.jx.parser.lambda.LambdaTemplate;
import com.oracle.truffle.jx.runtime.JXPartialLambda;

import java.util.List;

public class JXLambdaNode extends JXExpressionNode {


    private JXLambdaExecutor executor;

    private LambdaTemplate lambdaTemplate;
    public JXLambdaNode(
            TruffleLanguage<?> language,
            LambdaTemplate template,
            List<JXLambdaArgBindingNode> parameterBindingNodes,
            JXExpressionNode evalNode
    ) {
        this.lambdaTemplate = template;
        this.executor = new JXLambdaExecutor(language,template.getFrameDescriptor(), parameterBindingNodes, evalNode);
    }


    @Override
    public Object executeGeneric(VirtualFrame frame) {

        return new JXPartialLambda(
                this.executor.getCallTarget()
        ).execute(new Object[lambdaTemplate.parameterCount()]);
    }
}