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

package com.dirtyunicorns.duupdater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.dirtyunicorns.duupdater.Utils.Dialogs;
import com.dirtyunicorns.duupdater.Utils.FileObject;
import com.dirtyunicorns.duupdater.Utils.MainUtils;
import com.dirtyunicorns.duupdater.Utils.Vars;
import com.dirtyunicorns.duupdater.adapters.FileListAdapter;

/**
 * Created by mazwoz on 12/18/14.
 */
public class FolderFragment extends Fragment {

    private String dir;

    public FolderFragment() {
        this.setArguments(new Bundle());
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        ListView lv = (ListView) view.findViewById(android.R.id.list);

        Bundle bundle = getArguments();
        dir = bundle.getString("dir", "");

        Vars.files = MainUtils.getFiles(getActivity().getApplicationContext(), dir);

        if (Vars.files.length > 0) {

            FileListAdapter downloadsAdapter = new FileListAdapter(getActivity().getApplicationContext(), Vars.files);
            lv.setAdapter(downloadsAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FileObject downloadFile = (FileObject) parent.getItemAtPosition(position);
                    Dialogs.DownloadDialog(getActivity(), getString(R.string.download_prompt_message), getString(R.string.download_prompt_title), downloadFile);
                }
            });
        }

        return view;
    }

}
