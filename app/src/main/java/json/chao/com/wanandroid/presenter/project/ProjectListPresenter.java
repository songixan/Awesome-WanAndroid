package json.chao.com.wanandroid.presenter.project;

import javax.inject.Inject;

import json.chao.com.wanandroid.component.RxBus;
import json.chao.com.wanandroid.core.DataManager;
import json.chao.com.wanandroid.base.presenter.BasePresenter;
import json.chao.com.wanandroid.contract.project.ProjectListContract;
import json.chao.com.wanandroid.core.bean.BaseResponse;
import json.chao.com.wanandroid.core.bean.main.collect.FeedArticleData;
import json.chao.com.wanandroid.core.bean.main.collect.FeedArticleListData;
import json.chao.com.wanandroid.core.bean.project.ProjectListData;
import json.chao.com.wanandroid.core.bean.project.ProjectListResponse;
import json.chao.com.wanandroid.core.event.JumpToTheTopEvent;
import json.chao.com.wanandroid.utils.RxUtils;
import json.chao.com.wanandroid.widget.BaseObserver;

/**
 * @author quchao
 * @date 2018/2/24
 */

public class ProjectListPresenter extends BasePresenter<ProjectListContract.View> implements ProjectListContract.Presenter {

    private DataManager mDataManager;

    @Inject
    ProjectListPresenter(DataManager dataManager) {
        super(dataManager);
        this.mDataManager = dataManager;
    }

    @Override
    public void attachView(ProjectListContract.View view) {
        super.attachView(view);
        registerEvent();
    }

    private void registerEvent() {
        addSubscribe(RxBus.getDefault().toFlowable(JumpToTheTopEvent.class)
                .subscribe(jumpToTheTopEvent -> mView.showJumpToTheTop()));
    }


    @Override
    public void getProjectListData(int page, int cid) {
        addSubscribe(mDataManager.getProjectListData(page, cid)
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseObserver<BaseResponse<ProjectListData>>(mView) {
                            @Override
                            public void onNext(BaseResponse<ProjectListData> projectListResponse) {
                                if (projectListResponse.getErrorCode() == BaseResponse.SUCCESS) {
                                    mView.showProjectListData(projectListResponse);
                                } else {
                                    mView.showProjectListFail();
                                }
                            }
                        }));
    }

    @Override
    public void addCollectOutsideArticle(int position, FeedArticleData feedArticleData) {
        addSubscribe(mDataManager.addCollectOutsideArticle(feedArticleData.getTitle(),
                feedArticleData.getAuthor(), feedArticleData.getLink())
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseObserver<BaseResponse<FeedArticleListData>>(mView) {
                            @Override
                            public void onNext(BaseResponse<FeedArticleListData> feedArticleListResponse) {
                                if (feedArticleListResponse.getErrorCode() == BaseResponse.SUCCESS) {
                                    feedArticleData.setCollect(true);
                                    mView.showCollectOutsideArticle(position, feedArticleData, feedArticleListResponse);
                                } else {
                                    mView.showCollectFail();
                                }
                            }
                        }));
    }

    @Override
    public void cancelCollectArticle(int position, FeedArticleData feedArticleData) {
        addSubscribe(mDataManager.cancelCollectArticle(feedArticleData.getId())
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseObserver<BaseResponse<FeedArticleListData>>(mView) {
                            @Override
                            public void onNext(BaseResponse<FeedArticleListData> feedArticleListResponse) {
                                if (feedArticleListResponse.getErrorCode() == BaseResponse.SUCCESS) {
                                    feedArticleData.setCollect(false);
                                    mView.showCancelCollectArticleData(position, feedArticleData, feedArticleListResponse);
                                } else {
                                    mView.showCancelCollectFail();
                                }
                            }
                        }));
    }


}
