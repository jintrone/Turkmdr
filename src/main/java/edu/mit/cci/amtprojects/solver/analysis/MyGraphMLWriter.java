package edu.mit.cci.amtprojects.solver.analysis;

import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;
import org.apache.commons.collections15.Transformer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/17/12
 * Time: 8:09 AM
 */
public class MyGraphMLWriter<V,E> extends GraphMLWriter<V,E> {

    Map<String,String>  attributeClasses = new HashMap<String, String>();

    protected void writeKeySpecification(String key, String type,
			GraphMLMetadata<?> ds, BufferedWriter bw) throws IOException
	{
		bw.write("<key attr.name=\""+key+"\" attr.type=\""+attributeClasses.get(key)+"\" id=\"" + key + "\" for=\"" + type + "\"");
		boolean closed = false;
		// write out description if any
		String desc = ds.description;
		if (desc != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<desc>" + desc + "</desc>\n");
		}
		// write out default if any
		Object def = ds.default_value;
		if (def != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<default>" + def.toString() + "</default>\n");
		}
		if (!closed)
		    bw.write("/>\n");
		else
		    bw.write("</key>\n");
	}

    public void addVertexData(String id, String description, String clazz, String default_value,
			Transformer<V, String> vertex_transformer)
	{
		addVertexData(id,description,default_value,vertex_transformer);
        this.attributeClasses.put(id,clazz);
	}

}
