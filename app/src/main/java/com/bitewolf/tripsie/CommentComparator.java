package com.bitewolf.tripsie;

import org.w3c.dom.Comment;

import java.util.Comparator;

/**
 * Created by Travis on 2/25/2015.
 */
public class CommentComparator implements Comparator<TripComment> {

        @Override
        public int compare(TripComment lhs, TripComment rhs) {

            return  (int)(rhs.Date.getMillis() - lhs.Date.getMillis());

        }


}
