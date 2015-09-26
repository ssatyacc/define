package in.workarounds.define.portal;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import in.workarounds.define.R;
import in.workarounds.define.network.DaggerNetworkComponent;
import in.workarounds.define.network.NetworkModule;
import in.workarounds.define.meaning.MeaningPage;
import in.workarounds.define.ui.view.SelectableTextView;
import in.workarounds.define.urban.DaggerUrbanComponent;
import in.workarounds.define.urban.UrbanComponent;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.wordnet.DaggerWordnetComponent;
import in.workarounds.define.wordnet.WordnetComponent;
import in.workarounds.portal.Portal;

/**
 * Created by madki on 20/09/15.
 */
public class MainPortal extends Portal implements ComponentProvider {
    private static final String TAG = LogUtils.makeLogTag(MainPortal.class);
    public static final String BUNDLE_KEY_CLIP_TEXT = "bundle_key_clip_text";
    private String mClipText;
    private SelectableTextView mTvClipText;
    private View mPortalContainer;
    private PortalComponent component;
    private WordnetComponent wordnetComponent;
    private UrbanComponent urbanComponent;
    private MeaningPage wordnetPage;
    private MeaningPage urbanPage;


    public MainPortal(Context base) {
        super(base);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initComponents();
        setContentView(R.layout.portal_main);
        initViews();
        extractClipText(bundle);
        setClipTextToCard();
    }

    private void initComponents() {
        component = DaggerPortalComponent.create();
        wordnetComponent = DaggerWordnetComponent.create();
        urbanComponent = DaggerUrbanComponent.builder()
                .networkComponent(DaggerNetworkComponent.builder()
                        .networkModule(new NetworkModule(this))
                        .build())
                .build();
    }

    @Override
    public PortalComponent component() {
        return component;
    }

    @Override
    public void inject(MeaningPage meaningPage) {
        switch (meaningPage.getId()) {
            case R.id.wordnet_page:
                wordnetComponent.inject(meaningPage);
                break;
            case R.id.urban_page:
                urbanComponent.inject(meaningPage);
                break;
            default:
                throw new IllegalArgumentException("Meaning page must have a id. Either wordnet_page or urban_page");
        }
    }

    private void initViews() {
        mPortalContainer = findViewById(R.id.ll_main_portal_container);
        mTvClipText = (SelectableTextView) findViewById(R.id.tv_clip_text);

        mPortalContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wordnetPage = (MeaningPage) findViewById(R.id.wordnet_page);
        urbanPage = (MeaningPage) findViewById(R.id.urban_page);

        mTvClipText.setOnWordSelectedListener(new SelectableTextView.OnWordSelectedListener() {
            @Override
            public void onWordSelected(String word) {
                Toast.makeText(MainPortal.this, "Word clicked: " + word, Toast.LENGTH_LONG).show();
                wordnetPage.setWord(word);
                urbanPage.setWord(word);
            }
        });
    }

    private void extractClipText(Bundle bundle) {
        if(bundle != null && bundle.containsKey(BUNDLE_KEY_CLIP_TEXT)) {
            LogUtils.LOGD(TAG, "bundle clip text: " + bundle.getString(BUNDLE_KEY_CLIP_TEXT));
            mClipText = bundle.getString(BUNDLE_KEY_CLIP_TEXT);
        }
    }

    private void setClipTextToCard() {
        if(mClipText != null) {
            mTvClipText.setSelectableText(mClipText);
        }
    }


}