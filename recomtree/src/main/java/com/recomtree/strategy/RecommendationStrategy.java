package com.recomtree.strategy;

import com.recomtree.composite.CatalogComponent;
import com.recomtree.composite.Movie;
import java.util.List;

public interface RecommendationStrategy {
    List<Movie> recommend(CatalogComponent root, String parameter);
}