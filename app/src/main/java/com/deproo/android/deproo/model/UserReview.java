package com.deproo.android.deproo.model;

import com.deproo.android.deproo.utils.Constants;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName(Constants.ParseTable.TableUserReview.NAME)
public class UserReview extends ParseObject {

    public UserReview() {

    }

    public String getReview() {
        return getString(Constants.ParseTable.TableUserReview.REVIEW);
    }

    public void setReview(String review) {
        put(Constants.ParseTable.TableUserReview.REVIEW, review);
    }

    public double getRating() {
        return getDouble(Constants.ParseTable.TableUserReview.RATING);
    }

    public void setRating(double rating) {
        put(Constants.ParseTable.TableUserReview.RATING, rating);
    }

    public Broker getBrokerWhoGiveRating() {
        return (Broker) getParseUser(Constants.ParseTable.TableUserReview.RATING_FROM);
    }

    public void setBrokerWhoGiveRating(Broker broker) {
        put(Constants.ParseTable.TableUserReview.RATING_FROM, broker);
    }

    public void setBroker(Broker broker) {
        put(Constants.ParseTable.TableUserReview.USER, broker);
    }
}
