package com.example.salesforcemanagement.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.salesforcemanagement.model.Notelisting;

import java.util.List;

/**
 * Notes Data Object access help to access the notes
 * Created by ixi.Dv on 20/06/2018.
 */
@Dao
public interface NotesDaolisting {
    /**
     * Insert and save note to Database
     *
     * @param note
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    default void insertNote(Notelisting note) {

    }

    /**
     * Delete note
     *
     * @param note that will be delete
     */
    @Delete
    default void deleteNote(Notelisting... note) {

    }

    /**
     * Update note
     *
     * @param note the note that will be update
     */
    @Update
    default void updateNote(Notelisting note) {

    }

    /**
     * List All Notes From Database
     *
     * @return list of Notes
     */
    @Query("SELECT * FROM notes")
    List<Notelisting> getNotes();

    /**
     * @param noteId note id
     * @return Note
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    Notelisting getNoteById(int noteId);

    /**
     * Delete Note by Id from DataBase
     *
     * @param noteId
     */
    @Query("DELETE FROM notes WHERE id = :noteId")
    void deleteNoteById(int noteId);

}
