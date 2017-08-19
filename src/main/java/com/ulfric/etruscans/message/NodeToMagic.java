package com.ulfric.etruscans.message;

import org.w3c.dom.Node;

import com.ulfric.commons.xml.XmlHelper;
import com.ulfric.fancymessage.Message;

public enum NodeToMagic implements NodeToMessage {

	INSTANCE;

	@Override
	public final CompiledMessage apply(Node node) {
		Message base = new Message();
		base.setObfuscated(Boolean.TRUE);
		base.setText(XmlHelper.getNodeValue(node));
		return CompiledMessage.wrap(base);
	}

}