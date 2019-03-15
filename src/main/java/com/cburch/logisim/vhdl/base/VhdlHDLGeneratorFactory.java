package com.cburch.logisim.vhdl.base;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.fpga.designrulecheck.Netlist;
import com.cburch.logisim.fpga.designrulecheck.NetlistComponent;
import com.cburch.logisim.fpga.fpgagui.FPGAReport;
import com.cburch.logisim.fpga.hdlgenerator.AbstractHDLGeneratorFactory;
import com.cburch.logisim.fpga.hdlgenerator.FileWriter;
import com.cburch.logisim.fpga.hdlgenerator.HDLGeneratorFactory;
import com.cburch.logisim.instance.Port;

public class VhdlHDLGeneratorFactory extends AbstractHDLGeneratorFactory  {


	@Override
	public ArrayList<String> GetArchitecture(Netlist TheNetlist,
			AttributeSet attrs, String ComponentName, FPGAReport Reporter,
			String HDLType) {
		ArrayList<String> contents = new ArrayList<String>();
		contents.addAll(FileWriter.getGenerateRemark(ComponentName, HDLType,
				TheNetlist.projName()));

		VhdlContent content = ((VhdlEntityAttributes) attrs).getContent();;
		contents.add(content.getLibraries());
		contents.add(content.getArchitecture());

		return contents;
	}

	@Override
	public SortedMap<String, Integer> GetParameterMap(Netlist Nets,
			NetlistComponent ComponentInfo, FPGAReport Reporter) {
		AttributeSet attrs = ComponentInfo.GetComponent().getAttributeSet();
		VhdlContent content = ((VhdlEntityAttributes) attrs).getContent();
		SortedMap<String, Integer> ParameterMap = new TreeMap<String, Integer>();
                for (Attribute<Integer> a : content.getGenericAttributes()) {
                    VhdlEntityAttributes.VhdlGenericAttribute va = (VhdlEntityAttributes.VhdlGenericAttribute)a;
                    VhdlContent.Generic g = va.getGeneric();
                    Integer v = attrs.getValue(a);
                    if (v != null) {
                        ParameterMap.put(g.getName(), v);
                    } else {
                        ParameterMap.put(g.getName(), g.getDefaultValue());
                    }
                }
		return ParameterMap;
	}

	@Override
	public SortedMap<Integer, String> GetParameterList(AttributeSet attrs) {
		VhdlContent content = ((VhdlEntityAttributes) attrs).getContent();;
		SortedMap<Integer, String> Parameters = new TreeMap<Integer, String>();
                int i = -1;
                for (VhdlContent.Generic g : content.getGenerics()) {
                   Parameters.put(i--, g.getName());
                }
		return Parameters;
	}

	@Override
	public String getComponentStringIdentifier() {
		return "VHDL";
	}

	@Override
	public SortedMap<String, Integer> GetInputList(Netlist TheNetlist,
			AttributeSet attrs) {
		SortedMap<String, Integer> inputs = new TreeMap<String, Integer>();

		VhdlContent content = ((VhdlEntityAttributes) attrs).getContent();
		for (VhdlParser.PortDescription p : content.getPorts()) {
            if (p.getType() == Port.INPUT)
                    inputs.put(p.getName(), p.getWidth().getWidth());
        }

		return inputs;
	}

	@Override
	public SortedMap<String, Integer> GetOutputList(Netlist TheNetlist,
			AttributeSet attrs) {
		SortedMap<String, Integer> outputs = new TreeMap<String, Integer>();

		VhdlContent content = ((VhdlEntityAttributes) attrs).getContent();
		for (VhdlParser.PortDescription p : content.getPorts()) {
            if (p.getType() == Port.OUTPUT)
                    outputs.put(p.getName(), p.getWidth().getWidth());
        }

		return outputs;
	}

	@Override
	public SortedMap<String, String> GetPortMap(Netlist Nets,
			NetlistComponent ComponentInfo, FPGAReport Reporter, String HDLType) {
		SortedMap<String, String> PortMap = new TreeMap<String, String>();

		AttributeSet attrs = ComponentInfo.GetComponent().getAttributeSet();
		VhdlContent content = ((VhdlEntityAttributes) attrs).getContent();

        int i = 0;
        for (VhdlParser.PortDescription p : content.getPorts()) {
        	PortMap.putAll(GetNetMap(p.getName(), true,
        			ComponentInfo, i++, Reporter, HDLType, Nets));
        }
		return PortMap;
	}

	@Override
	public String GetSubDir() {
		return "circuit";
	}

	@Override
	public boolean HDLTargetSupported(String HDLType, AttributeSet attrs) {
		return HDLType.equals(HDLGeneratorFactory.VHDL);
	}

}