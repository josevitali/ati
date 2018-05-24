package ar.edu.itba.ati.model.transformations;


import ar.edu.itba.ati.model.pictures.Picture;

public interface PictureTransformer {
    <T,R> Picture<R> transform(Picture<T> picture);
}
