package com.ThreadWeaver.prototype.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeRequest {
    private Long minSize;
    private Long maxSize;
}
