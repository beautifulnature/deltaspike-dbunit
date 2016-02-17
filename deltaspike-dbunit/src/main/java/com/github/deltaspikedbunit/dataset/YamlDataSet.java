/*
 * Copyright 2002-2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.deltaspikedbunit.dataset;

/**
 * Created by rafael-pestano on 22/07/2015.
 */
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlDataSet implements IDataSet {

  private Map<String, MyTable> tables = new HashMap<String, MyTable>();

  public YamlDataSet(InputStream source) {
    @SuppressWarnings("unchecked")
    Map<String, List<Map<String, Object>>> data = (Map<String, List<Map<String, Object>>>) new Yaml().load(source);
    for (Map.Entry<String, List<Map<String, Object>>> ent : data.entrySet()) {
      String tableName = ent.getKey();
      List<Map<String, Object>> rows = ent.getValue();
      createTable(tableName.toUpperCase(), rows);
    }
  }

  class MyTable implements ITable {
    String                    name;

    List<Map<String, Object>> data;

    ITableMetaData            meta;

    MyTable(String name, List<String> columnNames) {
      this.name = name;
      this.data = new ArrayList<Map<String, Object>>();
      meta = createMeta(name, columnNames);
    }

    ITableMetaData createMeta(String name, List<String> columnNames) {
      Column[] columns = null;
      if (columnNames != null) {
        columns = new Column[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i++)
          columns[i] = new Column(columnNames.get(i), DataType.UNKNOWN);
      }
      return new DefaultTableMetaData(name, columns);
    }

    public int getRowCount() {
      return data.size();
    }

    public ITableMetaData getTableMetaData() {
      return meta;
    }

    public Object getValue(int row, String column) throws DataSetException {
      if (data.size() <= row)
        throw new RowOutOfBoundsException("" + row);
      return data.get(row).get(column.toUpperCase());
    }

    public void addRow(Map<String, Object> values) {
      data.add(convertMap(values));
    }

    Map<String, Object> convertMap(Map<String, Object> values) {
      Map<String, Object> ret = new HashMap<String, Object>();
      for (Map.Entry<String, Object> ent : values.entrySet()) {
        ret.put(ent.getKey().toUpperCase(), ent.getValue());
      }
      return ret;
    }

  }

  MyTable createTable(String name, List<Map<String, Object>> rows) {
    MyTable table = new MyTable(name, rows.size() > 0 ? new ArrayList<String>(rows.get(0).keySet()) : null);
    for (Map<String, Object> values : rows)
      table.addRow(values);
    tables.put(name.toUpperCase(), table);
    return table;
  }

  public ITable getTable(String tableName) throws DataSetException {
    return tables.get(tableName.toUpperCase());
  }

  public ITableMetaData getTableMetaData(final String tableName) throws DataSetException {
    MyTable myTable = tables.get(tableName.toUpperCase());
    if(myTable != null){
      return tables.get(tableName.toUpperCase()).getTableMetaData();
    }
    return null;
  }

  public String[] getTableNames() throws DataSetException {
    return (String[]) tables.keySet().toArray(new String[tables.size()]);
  }

  public ITable[] getTables() throws DataSetException {
    return (ITable[]) tables.values().toArray(new ITable[tables.size()]);
  }

  public ITableIterator iterator() throws DataSetException {
    return new DefaultTableIterator(getTables());
  }

  public ITableIterator reverseIterator() throws DataSetException {
    return new DefaultTableIterator(getTables(), true);
  }

  public boolean isCaseSensitiveTableNames() {
    return false;
  }

}
