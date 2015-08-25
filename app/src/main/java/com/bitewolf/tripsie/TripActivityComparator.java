package com.bitewolf.tripsie;

import java.util.Comparator;

/**
 * Created by Travis on 2/25/2015.
 */
public class TripActivityComparator implements Comparator<TripActivity> {

        @Override
        public int compare(TripActivity lhs, TripActivity rhs) {

            int difference = 0;
            int lhsVotes=0;
            int rhsVotes=0;

            try {
                for (int i = 0; i < lhs.TripActivityVotes.size(); i++) {
                    TripActivityVote vote = lhs.TripActivityVotes.get(i);

                    if (vote != null)
                        lhsVotes += vote.Vote;
                }

                for (int i = 0; i < rhs.TripActivityVotes.size(); i++) {
                    TripActivityVote vote = rhs.TripActivityVotes.get(i);

                    if (vote != null)
                        rhsVotes += vote.Vote;
                }
            }

            catch (Exception ex)
            {
                int i = 0;
            }

            if(lhs.IsComplete && !rhs.IsComplete)
            {
                return 1;
            }

            else if(!lhs.IsComplete && rhs.IsComplete)
            {
                return -1;
            }

            else {

                return rhsVotes - lhsVotes;
            }
        }

}
