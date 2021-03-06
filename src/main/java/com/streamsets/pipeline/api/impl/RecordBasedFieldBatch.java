/*
 * Copyright 2018 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.api.impl;

import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.FieldBatch;
import com.streamsets.pipeline.api.Record;

import java.util.Iterator;

/**
 * Implementation of FieldBatch that iterates over given paths in given record. Particularly suitable
 * for use in Record based processors.
 */
public class RecordBasedFieldBatch implements FieldBatch {

  /**
   * Record that this field batch is iterating on.
   */
  private Record record;

  /**
   * Iterator for field path that should be visited.
   */
  private Iterator<String> fieldPathIterator;

  /**
   * Current field path.
   */
  private String currentFieldPath;

  /**
   * Iterate over all field inside given record.
   *
   * @param record Record that is being processed
   */
  public RecordBasedFieldBatch(Record record) {
    this(record, record.getEscapedFieldPaths().iterator());
  }

  /**
   * Iterate over given paths inside the given record.
   *
   * @param record Record that is being processed.
   * @param fieldPaths Paths that should be processed.
   */
  public RecordBasedFieldBatch(Record record, Iterator<String> fieldPaths) {
    this.record = record;
    this.fieldPathIterator = fieldPaths;
    this.currentFieldPath = null;
  }

  @Override
  public boolean next() {
    if(!fieldPathIterator.hasNext()) {
      return false;
    }

    currentFieldPath = fieldPathIterator.next();
    return true;
  }

  @Override
  public String getFieldPath() {
    return currentFieldPath;
  }

  @Override
  public Field getField() {
    if(currentFieldPath != null) {
      return record.get(currentFieldPath);
    }

    return null;
  }

  @Override
  public Record getRecord() {
    return record;
  }

  @Override
  public void replace(Field replacement) {
    if(currentFieldPath != null) {
      record.set(currentFieldPath, replacement);
    }
  }

  @Override
  public void drop() {
    if(currentFieldPath != null) {
      record.delete(currentFieldPath);
    }
  }
}
