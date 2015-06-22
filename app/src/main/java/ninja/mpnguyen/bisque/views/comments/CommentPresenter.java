package ninja.mpnguyen.bisque.views.comments;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.XMLReader;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.chowders.things.json.Comment;

public class CommentPresenter {
    public static CommentViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_comment, viewGroup, false);
        return new CommentViewHolder(postView);
    }

    public static void bindListItem(CommentViewHolder holder, Comment comment) {
        Spanned spanned = processCommentText(comment);
        holder.comment_text.setText(spanned);
        holder.comment_text.setMovementMethod(LinkMovementMethod.getInstance());
        Spanned heading = getHeading(comment);
        holder.comment_author.setText(heading);

        ViewGroup.LayoutParams layoutParams = holder.padding.getLayoutParams();
        layoutParams.width = (comment.indent_level - 1) * 40;
        holder.padding.setLayoutParams(layoutParams);
    }

    private static Spanned getHeading(Comment comment) {
        SpannableStringBuilder b = new SpannableStringBuilder(comment.commenting_user.username);
        b.setSpan(new StyleSpan(Typeface.BOLD), 0, b.length(), 0);
        b.append(" ").append(Integer.toString(comment.score)).append(" points ");
        return b;
    }

    /**
     * Converts html text into an spanned string that handles trailing whitespace
     * and code segments more appropirately than {@link Html#fromHtml(String)}
     **/
    private static Spanned processCommentText(Comment comment) {
        String commentText = comment.comment.trim();

        // Handle code segments of the text.
        // This means preserve whitespace and typeset in monospace
        Document doc = preserveWhitespaceForCode(commentText);
        Spanned spanned = Html.fromHtml(doc.body().html(), null, new MonospaceForCodeHandler());

        // If we have trailing newlines in this processed spanned, then remove them.
        // There should at most ever be two.  This happens when we terminate the
        // comment with some div or paragraph and looks unsightly.
        if (spanned instanceof SpannableStringBuilder) {
            SpannableStringBuilder builder = (SpannableStringBuilder) spanned;
            int len = builder.length();
            if (len > 0 && builder.charAt(len - 1) == '\n') {
                if (len > 1 && builder.charAt(len - 2) == '\n') {
                    builder = builder.delete(len - 2, len);
                } else {
                    builder = builder.delete(len - 1, len);
                }
            }

            spanned = builder;
        }

        return spanned;
    }


    /**
     * Preprocess code segments.  In code segments, replace all
     * newlines with line breaks and spaces with non-breaking spaces
     * This will help preserve whitespace when viewing code blocks
     * Our custom tag handler will handle applying a monospace
     * font to the text
     */
    private static Document preserveWhitespaceForCode(String commentText) {
        Document doc = Jsoup.parse(commentText);
        Elements codes = doc.select("code");
        for (Element e : codes) {
            String encoded = TextUtils.htmlEncode(e.text().replaceAll(" ", "\u00A0"));
            encoded = encoded.replaceAll("\n", "<br>");
            e.html(encoded);
        }
        return doc;
    }

    /**
     * Typesets code segments in monospace font
     */
    private static class MonospaceForCodeHandler implements Html.TagHandler {
        private Integer start;
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equalsIgnoreCase("code")) {
                if (opening) {
                    if (start == null) start = output.length();
                }

                if (!opening && start != null) {
                    int end = output.length();
                    int flags = 0;
                    output.setSpan(new TypefaceSpan("monospace"), start, end, flags);
                    start = null;
                }
            }
        }
    }
}
