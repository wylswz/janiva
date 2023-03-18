package com.oracle.truffle.jx.nodes.core;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.jx.nodes.JXExpressionNode;
import com.oracle.truffle.jx.parser.lambda.LambdaTemplate;
import com.oracle.truffle.jx.runtime.exceptions.JXRuntimeException;

public class JXLambdaAttrAccessNode extends JXExpressionNode {

    private final TruffleString name;
    private LambdaTemplate lambdaTemplate;

    public JXLambdaAttrAccessNode(TruffleString name, LambdaTemplate lambdaTemplate) {
        this.name = name;
        this.lambdaTemplate = lambdaTemplate;
    }


    @Override
    public Object executeGeneric(VirtualFrame frame) {
        int slot=-1;
        for (int i=0; i<lambdaTemplate.parameterCount(); i++) {
            if (name.equals(lambdaTemplate.getParameterNames().get(i))) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            throw new JXRuntimeException("Cannot resolve attribute " + this.name);
        }
        return frame.getArguments()[slot];
    }
}