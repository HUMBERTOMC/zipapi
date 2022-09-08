package com.vercator.zipapi.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ZipFile {

    private String id;

    private float sizeMB;

    private String downloadURI;
}
