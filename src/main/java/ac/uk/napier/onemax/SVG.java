package ac.uk.napier.onemax;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;


public class SVG extends Component{

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, 100, 100);
	}
	public static void main(String[] args){
		SVG svg = new SVG();
		try {
			SVG.save(svg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	public static void save(Component component) throws Exception{
		 // Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        
        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        component.paint(svgGenerator);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
//        Writer out = new OutputStreamWriter(System.out, "UTF-8");
        Writer out = new FileWriter("" + System.currentTimeMillis() + ".svg");               
        svgGenerator.stream(out, useCSS);
        
        out.close();
	}
}
