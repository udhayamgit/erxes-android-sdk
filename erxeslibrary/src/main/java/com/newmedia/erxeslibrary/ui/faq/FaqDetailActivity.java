package com.newmedia.erxeslibrary.ui.faq;

import android.app.Service;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.newmedia.erxeslibrary.R;
import com.newmedia.erxeslibrary.configuration.Config;
import com.newmedia.erxeslibrary.configuration.Helper;
import com.newmedia.erxeslibrary.configuration.SoftKeyboard;
import com.newmedia.erxeslibrary.model.KnowledgeBaseArticle;
import com.newmedia.erxeslibrary.model.KnowledgeBaseCategory;

public class FaqDetailActivity extends AppCompatActivity {
    private ViewGroup container;
    private Point size;
    private Config config;
    private TextView general,article_header,date,content1,content2;
    private ImageView backImageView, cancelImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_faq_detail);
        config = Config.getInstance(this);
        load_findViewByid();
    }
    private void load_findViewByid(){
        container = this.findViewById(R.id.container);
        backImageView = this.findViewById(R.id.backImageView);
        cancelImageView = this.findViewById(R.id.cancelImageView);
        initIcon();

        size = Helper.display_configure(this,container,"#00000000");
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);

        SoftKeyboard softKeyboard;
        softKeyboard = new SoftKeyboard((ViewGroup)this.findViewById(R.id.linearlayout), im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                FaqDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        container.getLayoutParams().height =size.y*8/10;
                        container.requestLayout();
                    }
                });
            }
            @Override
            public void onSoftKeyboardShow() {
                FaqDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        container.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
                        container.requestLayout();
                    }
                });
            }
        });
        this.findViewById(R.id.info_header).setBackgroundColor(config.colorCode);
        cancelImageView.setOnClickListener(v -> logout());
        backImageView.setOnClickListener(v -> finish());

        general = this.findViewById(R.id.general);
        article_header = this.findViewById(R.id.article_header);
        date = this.findViewById(R.id.article_date);
        content1 = this.findViewById(R.id.article_content1);
        content2 = this.findViewById(R.id.article_content2);

        String id = getIntent().getStringExtra("id");
        String categoryId = getIntent().getStringExtra("categoryId");

        if(id!=null) {
            KnowledgeBaseCategory knowledgeBaseCategory = null;
            for (int i = 0; i < config.knowledgeBaseTopic.categories.size(); i ++) {
                if (config.knowledgeBaseTopic.categories.get(i)._id.equals(categoryId)) {
                    knowledgeBaseCategory = config.knowledgeBaseTopic.categories.get(i);
                    break;
                }
            }
            if (knowledgeBaseCategory != null) {
                KnowledgeBaseArticle knowledgeBaseArticle = null;
                for (int i = 0 ; i < knowledgeBaseCategory.articles.size(); i ++) {
                    if (knowledgeBaseCategory.articles.get(i)._id.equals(id)) {
                        knowledgeBaseArticle = knowledgeBaseCategory.articles.get(i);
                        break;
                    }
                }
                if (knowledgeBaseArticle != null) {
                    general.setText(knowledgeBaseArticle.title);
                    date.setText("Created : " + config.full_date(knowledgeBaseArticle.createdDate));
                    article_header.setText(knowledgeBaseArticle.title);
                    content1.setText(Html.fromHtml(knowledgeBaseArticle.summary));
                    content2.setText(Html.fromHtml(knowledgeBaseArticle.content));
                }
            }
        }
    }

    private void initIcon() {
        Glide.with(this).load(config.getBackIcon(this,config.getInColor(config.colorCode))).into(backImageView);
        Glide.with(this).load(config.getLogoutIcon(this,config.getInColor(config.colorCode))).into(cancelImageView);
    }

    public void logout(){
        config.Logout(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        config.setActivityConfig(this);
    }
}
