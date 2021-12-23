package pro.paulek.data.api;

import org.jetbrains.annotations.NotNull;

//TODO support for mariadb, mongodb
public enum DataModel {

    MYSQL,
    SQLITE;

    public static DataModel getModelByName(@NotNull String string) {
        if(string.equalsIgnoreCase("mysql")){
            return DataModel.MYSQL;
        }
        return DataModel.SQLITE;
    }

}
