package ninja.mpnguyen.bisque.views.comments;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
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
        String comment1 = comment.comment;
        Document dom = Jsoup.parse(comment1);
        Elements codes = dom.select("code");
        for (Element e : codes) {
            String encoded = TextUtils.htmlEncode(e.text().replaceAll(" ", "\u00A0"));
            encoded = encoded.replaceAll("\n", "<br>");
            e.html(encoded);
        }
        holder.comment_text.setText(Html.fromHtml(dom.body().html(), null, new CodeHandler()));
        holder.comment_author.setText(comment.commenting_user.username);

        ViewGroup.LayoutParams layoutParams = holder.padding.getLayoutParams();
        layoutParams.width = comment.indent_level * 40;
        holder.padding.setLayoutParams(layoutParams);
    }

    private static class CodeHandler implements Html.TagHandler {
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
