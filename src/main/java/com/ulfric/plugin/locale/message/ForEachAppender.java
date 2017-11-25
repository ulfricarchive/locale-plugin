package com.ulfric.plugin.locale.message;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ulfric.commons.xml.XmlHelper;

public class ForEachAppender extends ComplexAppender {

	@Override
	public CompiledMessage apply(Node append, CompiledMessage to) {
		NamedNodeMap nodes = append.getAttributes();
		VariableSequence iterableVariable = getIterableVariable(nodes.getNamedItem("collection"));
		String elementVariable = getNewDetailName(nodes, iterableVariable);
		CompiledMessage delimiter = delimiter(nodes);

		CompiledMessage continuation = new ForEachCompiledMessage(iterableVariable, elementVariable, delimiter);
		to.addChild(continuation);
		return continuation;
	}

	private String getNewDetailName(NamedNodeMap map, VariableSequence variable) {
		Node element = map.getNamedItem("element");
		if (element == null) {
			return elementNameFromVariable(variable);
		}

		String newDetailName = XmlHelper.getNodeValue(element);
		return newDetailName == null ? elementNameFromVariable(variable) : newDetailName;
	}

	private String elementNameFromVariable(VariableSequence variableSequence) {
		String variable = variableSequence.getVariable();
		int size = variable.length();
		return size > 1 && variable.endsWith("s") ? variable.substring(0, size - 1) : variable;
	}

	private CompiledMessage delimiter(NamedNodeMap nodes) {
		Node delimiter = nodes.getNamedItem("delimiter");

		if (delimiter == null) {
			return null;
		}

		String value = XmlHelper.getNodeValue(delimiter);
		return value == null ? null : CompiledMessage.compile(value);
	}

}