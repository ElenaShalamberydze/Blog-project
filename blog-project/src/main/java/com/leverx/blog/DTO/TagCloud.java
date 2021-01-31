package com.leverx.blog.DTO;

import java.util.Objects;

public class TagCloud {

    private String tag;
    private long postCount;

    public TagCloud(String tag, long postCount) {
        this.tag = tag;
        this.postCount = postCount;
    }

    public TagCloud() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getPostCount() {
        return postCount;
    }

    public void setPostCount(long postCount) {
        this.postCount = postCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagCloud tagCloud = (TagCloud) o;
        return postCount == tagCloud.postCount &&
                Objects.equals(tag, tagCloud.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, postCount);
    }

    @Override
    public String toString() {
        return "TagCloud{" +
                "tag='" + tag + '\'' +
                ", postCount=" + postCount +
                '}';
    }
}
