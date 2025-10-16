package com.simple.taxi.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YandexGeocodeResponse {
    private Response response;

    @Getter
    @Setter
    public static class Response {
        private GeoObjectCollection GeoObjectCollection;
    }

    @Getter
    @Setter
    public static class GeoObjectCollection {
        private List<FeatureMember> featureMember;
    }

    @Getter
    @Setter
    public static class FeatureMember {
        private GeoObject GeoObject;
    }

    @Getter
    @Setter
    public static class GeoObject {
        private String name; // название объекта
        private PointPoint pos; // координаты "37.617635 55.755814"
        private MetaDataProperty metaDataProperty;
    }

    @Getter
    @Setter
    public static class PointPoint {
        private String pos; // "долгота широта"
    }

    @Getter
    @Setter
    public static class MetaDataProperty {
        private GeocoderMetaData GeocoderMetaData;
    }

    @Getter
    @Setter
    public static class GeocoderMetaData {
        private String text; // полный адрес
        private AddressDetails AddressDetails;
    }

    @Getter
    @Setter
    public static class AddressDetails {
        private Country Country;
    }

    @Getter
    @Setter
    public static class Country {
        private AdministrativeArea AdministrativeArea;
    }

    @Getter
    @Setter
    public static class AdministrativeArea {
        private SubAdministrativeArea SubAdministrativeArea;
        private Locality Locality;
    }

    @Getter
    @Setter
    public static class SubAdministrativeArea {
        private Locality Locality;
    }

    @Getter
    @Setter
    public static class Locality {
        private String LocalityName;
        private Thoroughfare Thoroughfare;
    }

    @Getter
    @Setter
    public static class Thoroughfare {
        private String ThoroughfareName;
        private Premise Premise;
    }

    @Getter
    @Setter
    public static class Premise {
        private String PremiseNumber;
    }
}
