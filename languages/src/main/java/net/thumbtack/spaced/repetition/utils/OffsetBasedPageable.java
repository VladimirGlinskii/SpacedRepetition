package net.thumbtack.spaced.repetition.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Objects;

public class OffsetBasedPageable implements Pageable, Serializable {

    private final long offset;
    private final int limit;
    private final Sort sort;

    public OffsetBasedPageable(long offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public OffsetBasedPageable(long offset, int limit, Sort.Direction direction,
                               String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetBasedPageable)) return false;
        OffsetBasedPageable pageable = (OffsetBasedPageable) o;
        return getOffset() == pageable.getOffset() &&
                limit == pageable.limit &&
                Objects.equals(getSort(), pageable.getSort());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOffset(), limit, getSort());
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageable(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    private Pageable previous() {
        return new OffsetBasedPageable(getOffset() - getPageSize(), getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageable(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
