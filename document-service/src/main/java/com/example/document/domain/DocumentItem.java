package com.example.document.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentItem {
    private String id;
    private String vin;
    private String name;
    private String type;
    private String url;
    private String createdAt;
    private String source;
}
