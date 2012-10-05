package edu.mit.cci.amtprojects.kickball;

import com.sun.tools.internal.ws.wsdl.document.jaxws.*;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import org.apache.log4j.Logger;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 8:01 AM
 */
public class QuoteUtilities {
    
    public static String quoteStartStr = "\\[quote(?:[^=\\]\\s]+\\]|=\\&quot;(.+?)\\&quot;[^\\]\\s]+\\])";
    private static String quoteEndStr= "\\[/quote:[^\\]\\s]+\\]";
    public static Pattern quoteStart = Pattern.compile(quoteStartStr, Pattern.DOTALL);
    public static Pattern quoteEnd = Pattern.compile(quoteEndStr, Pattern.DOTALL);
    private static Logger log = Logger.getLogger(QuoteUtilities.class);
    
      public static class Quote {

        //where this quote comes from
        Post origin;
        Post context;

        //where this quote resides in the current context
        int location,qstart,qend;



        String _text;

        String cleanedText;

        String author;

        public Quote(Post context, String author, int location) {
            this.author = author;
            this.location = location;
            this.context = context;
        }

        public void setText(String text) {
            this._text = text;
            cleanedText = text.replaceAll(quoteStartStr,"<div class=\"quote\">").replaceAll(quoteEndStr,"</div>").replaceAll("\\s","");

        }

        public String getCleanedText() {
            return cleanedText;
        }

        public String getText() {
            return _text;
        }


    }
    
    public static class QMark implements Comparable<QMark> {

        public boolean open;
        public int start;
        public int end;
        public String user;

        public QMark(boolean open, int start, int end, String user) {
            this.open = open;
           this.start = start;
            this.end = end;
            this.user = user;
        }

        public int compareTo(QMark qMark) {
            return this.start - qMark.start;
        }
    }
    
     public static Collection<Quote> getEmbeddedQuote(String content) throws MalformedQuoteException {


        Matcher m = quoteStart.matcher(content);
        List<QMark> marks = new ArrayList<QMark>();
        while (m.find()) {
            marks.add(new QMark(true, m.start(),m.end(), m.group(1)));
        }
        if (marks.isEmpty()) {
            return Collections.emptyList();
        }
        m = quoteEnd.matcher(content);
        while (m.find()) {
            marks.add(new QMark(false, m.start(),m.end(), null));
        }
        if (marks.size() != (marks.size() / 2) * 2) {
            throw new MalformedQuoteException("Unbalanced quotes");
        }

        List<Quote> result = new ArrayList<Quote>();
        Collections.sort(marks);

        QMark first = null;
        int count = 0;
        for (QMark mark : marks) {
            if (mark.open) {
                count++;
                if (first == null) {
                    first = mark;
                }
            } else {
                count--;

                if (count == 0) {
                    if (first != null) {

                        String text = content.substring(first.end, mark.start);
                        if (text == null || text.isEmpty()) {
                            throw new MalformedQuoteException("Empty quote string");
                        }
                        Quote q = new Quote(null, first.user, first.end);
                        q.qstart = first.start;
                        q.qend = mark.end;
                        q.setText(text);
                        result.add(q);
                        first = null;
                    } else {
                        //summary();
                        throw new RuntimeException("You fucked up");
                    }
                }
            }
            if (count < 0) {
                throw new MalformedQuoteException("Unbalanced quotes");
            }
        }

        return result;
    }

    public static class MalformedQuoteException extends Exception {
        public MalformedQuoteException(String msg) {
            super(msg);
        }
    }


    public static String sanitize(String content) {
        Matcher m = quoteStart.matcher(content);
        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(buffer,"<div class=\"quote\">" +
                    (m.group(1) != null && !m.group(1).isEmpty() ? "<span class=\"author\">" + m.group(1) + " writes:</span>" : "") +
                    "<div class=\"content\">");
        }
        m.appendTail(buffer);
        content = buffer.toString();
        m = quoteEnd.matcher(content);

        while (m.find()) {
            content = m.replaceAll("</div></div>");
        }

        content = content.replaceAll("\\\\n","<br/>");

        return content;
    }

    public static void main(String[] args) {
        String s = "thanks, i try.\\n\\nyes, fearthefro?";
        System.out.println(sanitize(s));
    }
}
