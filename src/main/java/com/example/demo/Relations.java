package com.example.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
public class Relations 
{
    private String TABLE_NAME;
    private String COLUMN_NAME;
    private String CONSTRAINT_NAME;
    private String REFERENCED_TABLE_NAME;
    private String REFERENCED_COLUMN_NAME;
}
