package sg.howard.twitterclient.base;

public interface BaseView<T> {
    void setPresenter( T presenter);

    void showLoading(boolean isShow);

    void showError(String message);
}
