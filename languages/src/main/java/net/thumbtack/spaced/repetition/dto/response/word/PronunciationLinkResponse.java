package net.thumbtack.spaced.repetition.dto.response.word;

public class PronunciationLinkResponse {
    String url;

    public PronunciationLinkResponse(String url) {
        this.url = url;
    }

    public PronunciationLinkResponse() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
