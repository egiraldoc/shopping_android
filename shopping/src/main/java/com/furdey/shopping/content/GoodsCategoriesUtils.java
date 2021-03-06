package com.furdey.shopping.content;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider.Columns;

public class GoodsCategoriesUtils {

	private static final int UNDEFINED = -1;

	/**
	 * Constructs model from the current cursor position
	 */
	public static GoodsCategory fromCursor(Cursor cursor) {
		GoodsCategory model = new GoodsCategory();

		int field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.NAME.toString());
		if (field != UNDEFINED)
			model.setName(cursor.getString(field));

		field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.DESCR.toString());
		if (field != UNDEFINED)
			model.setDescr(cursor.getString(field));

		field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.ICON.toString());
		if (field != UNDEFINED)
			model.setIcon(cursor.getString(field));

		return model;
	}

	private static final String[] goodsCategoriesListProjection = new String[] {
			GoodsCategoriesContentProvider.Columns._id.toString(),
			GoodsCategoriesContentProvider.Columns.NAME.toString(),
			GoodsCategoriesContentProvider.Columns.DESCR.toString(),
			GoodsCategoriesContentProvider.Columns.ICON.toString() };

	public static CursorLoader getGoodsCategoriesLoader(Context context, String[] projection,
			String selection, String[] selectionArgs, String orderBy) {
		return new CursorLoader(context, GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI,
				projection, selection, selectionArgs, orderBy);
	}

	@SuppressLint("DefaultLocale")
	public static CursorLoader getGoodsCategoriesLoader(Context context, String filter) {
		String selection = null;
		String[] selectionArgs = null;

		if (filter != null && filter.trim().length() > 0) {
			selection = "LOWER(" + Columns.NAME_LOWER.toString() + ") LIKE ?";
			selectionArgs = new String[] { "%" + filter.toLowerCase() + "%" };
		}

		return getGoodsCategoriesLoader(context, goodsCategoriesListProjection, selection,
				selectionArgs, null);
	}

	public static Cursor getGoodsCategories(Context context, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		return context.getContentResolver().query(GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI,
                projection, selection, selectionArgs, orderBy);
	}

	public static Cursor getGoodsCategoriesByName(Context context, String name) {
		return getGoodsCategories(context, goodsCategoriesListProjection,
                Columns.NAME_LOWER.toString() + " = ?", new String[]{name.toLowerCase()},
                null);
	}

    public static GoodsCategory getGoodsCategoryById(Context context, long id) {
        Cursor cursor = getGoodsCategories(context, goodsCategoriesListProjection,
                Columns._id.toString() + " = ?", new String[] { Long.toString(id) },
                null);
        GoodsCategory goodsCategory = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                goodsCategory = fromCursor(cursor);
            }

            cursor.close();
        }

        return goodsCategory;
    }

    public static ContentValues getContentValues(GoodsCategory goodsCategory, boolean includeId) {
		ContentValues contentValues = new ContentValues();

		if (goodsCategory.getId() != null && includeId)
			contentValues.put(GoodsCategoriesContentProvider.Columns._id.toString(),
					goodsCategory.getId());

		if (goodsCategory.getDescr() != null)
			contentValues.put(GoodsCategoriesContentProvider.Columns.DESCR.toString(),
					goodsCategory.getDescr());

		if (goodsCategory.getName() != null) {
            contentValues.put(GoodsCategoriesContentProvider.Columns.NAME.toString(),
                    goodsCategory.getName());
            contentValues.put(GoodsCategoriesContentProvider.Columns.NAME_LOWER.toString(),
                    goodsCategory.getName().toLowerCase());
        }

		if (goodsCategory.getIcon() != null)
			contentValues.put(GoodsCategoriesContentProvider.Columns.ICON.toString(), goodsCategory
					.getIcon().toString());

		return contentValues;
	}

	public static Uri saveGoodsCategory(Context context, GoodsCategory goodsCategory) {
		if (goodsCategory.getId() == null) {
			return context.getContentResolver().insert(
					GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI,
					getContentValues(goodsCategory, false));
		} else {
            context.getContentResolver().update(
                    ContentUris.withAppendedId(GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI,
                            goodsCategory.getId()), getContentValues(goodsCategory, false), null, null);
			return GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI;
		}
	}

	public static int deleteGoodsCategory(Context context, long goodsCategoryId) {
		return context.getContentResolver().delete(
				ContentUris.withAppendedId(GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI,
						goodsCategoryId), null, null);
	}

//    public static void addNameLowerIfNotExists(Context context) {
//        String[] projection = new String[] { Columns._id.toString(), Columns.NAME.toString() };
//        SearchUtils.addSearchFieldIfNotExists(context, Columns.NAME_LOWER.toString(), projection,
//                new SearchUtils.ObjectsProvider() {
//                    @Override
//                    public Cursor getObjects(Context context, String[] projection,
//                                             String selection) {
//                        return getGoodsCategories(context, projection, selection, null, null);
//                    }
//
//                    @Override
//                    public void saveObject(Context context, Cursor c) {
//                        GoodsCategory gc = fromCursor(c);
//                        saveGoodsCategory(context, gc);
//                    }
//
//                    @Override
//                    public void addColumn(Context context, String columnName) {
//                        GoodsContentProvider.addColumn(context, columnName, "VARCHAR");
//                    }
//                });
//    }
}
