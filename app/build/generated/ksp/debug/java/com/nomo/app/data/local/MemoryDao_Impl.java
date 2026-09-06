package com.nomo.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.nomo.app.data.sync.SyncStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MemoryDao_Impl implements MemoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MemoryEntity> __insertionAdapterOfMemoryEntity;

  private final EntityDeletionOrUpdateAdapter<MemoryEntity> __deletionAdapterOfMemoryEntity;

  private final EntityDeletionOrUpdateAdapter<MemoryEntity> __updateAdapterOfMemoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMemoryById;

  public MemoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMemoryEntity = new EntityInsertionAdapter<MemoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `memories` (`id`,`photoPath`,`latitude`,`longitude`,`timestamp`,`placeName`,`dishName`,`foodVibe`,`category`,`note`,`tripId`,`syncStatus`,`driveFileId`,`driveJsonId`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPhotoPath());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        statement.bindLong(5, entity.getTimestamp());
        statement.bindString(6, entity.getPlaceName());
        if (entity.getDishName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDishName());
        }
        if (entity.getFoodVibe() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getFoodVibe());
        }
        statement.bindString(9, entity.getCategory());
        if (entity.getNote() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNote());
        }
        if (entity.getTripId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTripId());
        }
        statement.bindString(12, __SyncStatus_enumToString(entity.getSyncStatus()));
        if (entity.getDriveFileId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDriveFileId());
        }
        if (entity.getDriveJsonId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getDriveJsonId());
        }
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfMemoryEntity = new EntityDeletionOrUpdateAdapter<MemoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `memories` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfMemoryEntity = new EntityDeletionOrUpdateAdapter<MemoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memories` SET `id` = ?,`photoPath` = ?,`latitude` = ?,`longitude` = ?,`timestamp` = ?,`placeName` = ?,`dishName` = ?,`foodVibe` = ?,`category` = ?,`note` = ?,`tripId` = ?,`syncStatus` = ?,`driveFileId` = ?,`driveJsonId` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPhotoPath());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        statement.bindLong(5, entity.getTimestamp());
        statement.bindString(6, entity.getPlaceName());
        if (entity.getDishName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDishName());
        }
        if (entity.getFoodVibe() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getFoodVibe());
        }
        statement.bindString(9, entity.getCategory());
        if (entity.getNote() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNote());
        }
        if (entity.getTripId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTripId());
        }
        statement.bindString(12, __SyncStatus_enumToString(entity.getSyncStatus()));
        if (entity.getDriveFileId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDriveFileId());
        }
        if (entity.getDriveJsonId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getDriveJsonId());
        }
        statement.bindLong(15, entity.getUpdatedAt());
        statement.bindString(16, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE memories SET syncStatus = ?, driveFileId = ?, driveJsonId = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMemoryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM memories WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMemory(final MemoryEntity memory,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMemoryEntity.insert(memory);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMemory(final MemoryEntity memory,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMemoryEntity.handle(memory);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMemory(final MemoryEntity memory,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMemoryEntity.handle(memory);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String id, final SyncStatus status, final String driveFileId,
      final String driveJsonId, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, __SyncStatus_enumToString(status));
        _argIndex = 2;
        if (driveFileId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, driveFileId);
        }
        _argIndex = 3;
        if (driveJsonId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, driveJsonId);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 5;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMemoryById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMemoryById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMemoryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MemoryEntity>> getAllMemoriesFlow() {
    final String _sql = "SELECT * FROM memories ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllMemories(final Continuation<? super List<MemoryEntity>> $completion) {
    final String _sql = "SELECT * FROM memories ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMemoryById(final String id,
      final Continuation<? super MemoryEntity> $completion) {
    final String _sql = "SELECT * FROM memories WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemoryEntity>() {
      @Override
      @Nullable
      public MemoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final MemoryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<MemoryEntity> getMemoryByIdFlow(final String id) {
    final String _sql = "SELECT * FROM memories WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<MemoryEntity>() {
      @Override
      @Nullable
      public MemoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final MemoryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MemoryEntity>> getMemoriesByCategoryFlow(final String category) {
    final String _sql = "SELECT * FROM memories WHERE category = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MemoryEntity>> getMemoriesByTripFlow(final String tripId) {
    final String _sql = "SELECT * FROM memories WHERE tripId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tripId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPendingSyncMemories(final Continuation<? super List<MemoryEntity>> $completion) {
    final String _sql = "SELECT * FROM memories WHERE syncStatus IN ('LOCAL_ONLY', 'RETRY_QUEUED', 'QUEUED', 'UPLOAD_FAILED')";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MemoryEntity>> getPendingSyncMemoriesFlow() {
    final String _sql = "SELECT * FROM memories WHERE syncStatus IN ('LOCAL_ONLY', 'RETRY_QUEUED', 'QUEUED', 'UPLOAD_FAILED')";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MemoryEntity>> getSyncedMemoriesFlow() {
    final String _sql = "SELECT * FROM memories WHERE syncStatus = 'SYNCED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MemoryEntity>> getMemoriesInBoundingBoxFlow(final double minLat,
      final double maxLat, final double minLng, final double maxLng) {
    final String _sql = "\n"
            + "        SELECT * FROM memories \n"
            + "        WHERE latitude BETWEEN ? AND ? \n"
            + "        AND longitude BETWEEN ? AND ? \n"
            + "        ORDER BY timestamp DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, minLat);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, maxLat);
    _argIndex = 3;
    _statement.bindDouble(_argIndex, minLng);
    _argIndex = 4;
    _statement.bindDouble(_argIndex, maxLng);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<MemoryEntity>>() {
      @Override
      @NonNull
      public List<MemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlaceName = CursorUtil.getColumnIndexOrThrow(_cursor, "placeName");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfFoodVibe = CursorUtil.getColumnIndexOrThrow(_cursor, "foodVibe");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfDriveJsonId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveJsonId");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MemoryEntity> _result = new ArrayList<MemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhotoPath;
            _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlaceName;
            _tmpPlaceName = _cursor.getString(_cursorIndexOfPlaceName);
            final String _tmpDishName;
            if (_cursor.isNull(_cursorIndexOfDishName)) {
              _tmpDishName = null;
            } else {
              _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            }
            final String _tmpFoodVibe;
            if (_cursor.isNull(_cursorIndexOfFoodVibe)) {
              _tmpFoodVibe = null;
            } else {
              _tmpFoodVibe = _cursor.getString(_cursorIndexOfFoodVibe);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpTripId;
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null;
            } else {
              _tmpTripId = _cursor.getString(_cursorIndexOfTripId);
            }
            final SyncStatus _tmpSyncStatus;
            _tmpSyncStatus = __SyncStatus_stringToEnum(_cursor.getString(_cursorIndexOfSyncStatus));
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            final String _tmpDriveJsonId;
            if (_cursor.isNull(_cursorIndexOfDriveJsonId)) {
              _tmpDriveJsonId = null;
            } else {
              _tmpDriveJsonId = _cursor.getString(_cursorIndexOfDriveJsonId);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MemoryEntity(_tmpId,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpPlaceName,_tmpDishName,_tmpFoodVibe,_tmpCategory,_tmpNote,_tmpTripId,_tmpSyncStatus,_tmpDriveFileId,_tmpDriveJsonId,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __SyncStatus_enumToString(@NonNull final SyncStatus _value) {
    switch (_value) {
      case LOCAL_ONLY: return "LOCAL_ONLY";
      case QUEUED: return "QUEUED";
      case UPLOADING: return "UPLOADING";
      case SYNCED: return "SYNCED";
      case UPLOAD_FAILED: return "UPLOAD_FAILED";
      case RETRY_QUEUED: return "RETRY_QUEUED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private SyncStatus __SyncStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "LOCAL_ONLY": return SyncStatus.LOCAL_ONLY;
      case "QUEUED": return SyncStatus.QUEUED;
      case "UPLOADING": return SyncStatus.UPLOADING;
      case "SYNCED": return SyncStatus.SYNCED;
      case "UPLOAD_FAILED": return SyncStatus.UPLOAD_FAILED;
      case "RETRY_QUEUED": return SyncStatus.RETRY_QUEUED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
