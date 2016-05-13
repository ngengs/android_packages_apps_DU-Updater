/*
* Copyright (C) 2015 Dirty Unicorns
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.dirtyunicorns.duupdater.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.Utils.FileObject;

/**
 * Created by mazwoz on 27.02.15.
 */
public class FileListAdapter  extends ArrayAdapter<FileObject>{

    private View row;
    private FileObject[] fObject;

    public FileListAdapter(Context context,FileObject[] files) {
        super(context, R.layout.list_file_item, files);

        fObject = files;
    }

    @SuppressLint("ViewHolder")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.list_file_item, parent, false);

        TextView txtFilename = (TextView) row.findViewById(R.id.txtFilename);

        txtFilename.setText(fObject[position].filename);

        return row;
    }
}
