package com.example.salesforcemanagement.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.salesforcemanagement.model.Note;
import com.example.salesforcemanagement.model.Notelisting;

/**
 * Created by ixi.Dv on 20/06/2018.
 */
@Database(entities = Notelisting.class, version = 1)
public abstract class NotesDBlisting extends RoomDatabase {
    public abstract NotesDaolisting notesDao();

    public static final String DATABSE_NAME = "notesDb";
    private static NotesDBlisting instance;

    public static NotesDBlisting getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, NotesDBlisting.class, DATABSE_NAME)
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
