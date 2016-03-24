package com.pabhinav.zovido.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.alertdialogs.ZovidoAlertInputDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DirectoryChoserDialog {
    private boolean m_isNewFolderEnabled = true;
    private String m_sdcardDirectory = "";
    private Context m_context;
    private TextView m_titleView;

    private String m_dir = "";
    private List<String> m_subdirs = null;
    private ChosenDirectoryListener m_chosenDirectoryListener = null;
    private ArrayAdapter<String> m_listAdapter = null;
    private AlertDialog dirsDialog;

    //////////////////////////////////////////////////////
    // Callback interface for selected directory
    //////////////////////////////////////////////////////
    public interface ChosenDirectoryListener
    {
        public void onChosenDir(String chosenDir);
    }

    public DirectoryChoserDialog(Context context, ChosenDirectoryListener chosenDirectoryListener)
    {
        m_context = context;
        m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_chosenDirectoryListener = chosenDirectoryListener;

        try
        {
            m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
        }
        catch (IOException ioe)
        {
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // setNewFolderEnabled() - enable/disable new folder button
    ///////////////////////////////////////////////////////////////////////

    public void setNewFolderEnabled(boolean isNewFolderEnabled)
    {
        m_isNewFolderEnabled = isNewFolderEnabled;
    }

    public boolean getNewFolderEnabled()
    {
        return m_isNewFolderEnabled;
    }

    ///////////////////////////////////////////////////////////////////////
    // chooseDirectory() - load directory chooser dialog for initial
    // default sdcard directory
    ///////////////////////////////////////////////////////////////////////

    public void chooseDirectory()
    {
        // Initial directory is sdcard directory
        chooseDirectory(m_sdcardDirectory);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // chooseDirectory(String dir) - load directory chooser dialog for initial
    // input 'dir' directory
    ////////////////////////////////////////////////////////////////////////////////

    public void chooseDirectory(String dir)
    {
        File dirFile = new File(dir);
        if (! dirFile.exists() || ! dirFile.isDirectory())
        {
            dir = m_sdcardDirectory;
        }

        try
        {
            dir = new File(dir).getCanonicalPath();
        }
        catch (IOException ioe)
        {
            return;
        }

        m_dir = dir;
        m_subdirs = getDirectories(dir);

        class DirectoryOnClickListener implements OnClickListener
        {
            public void onClick(DialogInterface dialog, int item)
            {
                // Navigate into the sub-directory
                m_dir += "/" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder =
                createDirectoryChooserDialog(dir, m_subdirs, new DirectoryOnClickListener());

        dialogBuilder.setPositiveButton("OK", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Current directory chosen
                if (m_chosenDirectoryListener != null)
                {
                    // Call registered listener supplied with the chosen directory
                    m_chosenDirectoryListener.onChosenDir(m_dir);
                }
            }
        }).setNegativeButton("Cancel", null);

        dirsDialog = dialogBuilder.create();

        dirsDialog.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    // Back button pressed
                    if ( m_dir.equals(m_sdcardDirectory) )
                    {
                        // The very top level directory, do nothing
                        return false;
                    }
                    else
                    {
                        // Navigate back to an upper directory
                        m_dir = new File(m_dir).getParent();
                        updateDirectory();
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        // Show directory chooser dialog
        dirsDialog.show();
    }

    private boolean createSubDir(String newDir)
    {
        File newDirFile = new File(newDir);
        if (! newDirFile.exists() )
        {
            return newDirFile.mkdir();
        }

        return false;
    }

    private List<String> getDirectories(String dir)
    {
        List<String> dirs = new ArrayList<String>();

        try
        {
            File dirFile = new File(dir);
            if (! dirFile.exists() || ! dirFile.isDirectory())
            {
                return dirs;
            }

            for (File file : dirFile.listFiles())
            {
                if ( file.isDirectory() )
                {
                    dirs.add( file.getName() );
                }
            }
        }
        catch (Exception e)
        {
        }

        Collections.sort(dirs, new Comparator<String>()
        {
            public int compare(String o1, String o2)
            {
                return o1.compareTo(o2);
            }
        });

        return dirs;
    }

    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             OnClickListener onClickListener)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);

        View rootView = ((Activity)m_context).getLayoutInflater().inflate(R.layout.file_choser_dialog, null);
        m_titleView = (TextView) rootView.findViewById(R.id.directory_text_view);
        TextView cancelTextView = (TextView)rootView.findViewById(R.id.cancel_directory_choser_button);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dirsDialog != null){
                    dirsDialog.cancel();
                }
            }
        });
        TextView selectTextView = (TextView)rootView.findViewById(R.id.select_button);
        selectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Current directory chosen
                if (m_chosenDirectoryListener != null)
                {
                    // Call registered listener supplied with the chosen directory
                    m_chosenDirectoryListener.onChosenDir(m_dir);
                }

                if(dirsDialog != null){
                    dirsDialog.cancel();
                }
            }
        });


        Button newFolderButton = (Button) rootView.findViewById(R.id.new_folder_button);
        newFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZovidoAlertInputDialog zovidoAlertInputDialog = new ZovidoAlertInputDialog(
                        m_context,
                        "New Folder Name",
                        "Please enter a new folder name below : ",
                        "folder name",
                        "Cancel",
                        "Create"
                );
                zovidoAlertInputDialog.show();
                zovidoAlertInputDialog.setOnAlertButtonClicked(new ZovidoAlertInputDialog.OnAlertButtonClicked() {
                    @Override
                    public void onLeftButtonClicked(View v) {}

                    @Override
                    public void onRightButtonClicked(View v, String editTextString) {

                        // Create new directory
                        if (createSubDir(m_dir + "/" + editTextString)) {
                            // Navigate into the new directory
                            m_dir += "/" + editTextString;
                            updateDirectory();
                        } else {
                            Toast.makeText(m_context, "Failed to create '" + editTextString + "' folder", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ListView myListView = (ListView)rootView.findViewById(R.id.my_list_view);
        m_listAdapter = createListAdapter(listItems);
        myListView.setAdapter(m_listAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_dir += "/" + m_listAdapter.getItem(position);
                updateDirectory();
            }
        });

        dialogBuilder.setCustomTitle(rootView);
        dialogBuilder.setCancelable(false);

        return dialogBuilder;
    }

    private void updateDirectory()
    {
        m_subdirs.clear();
        m_subdirs.addAll( getDirectories(m_dir) );
        m_titleView.setText(m_dir);

        m_listAdapter.notifyDataSetChanged();
    }

    private ArrayAdapter<String> createListAdapter(List<String> items)
    {
        return new ArrayAdapter<String>(m_context,
                android.R.layout.select_dialog_item, android.R.id.text1, items)
        {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);

                if (v instanceof TextView)
                {
                    // Enable list item (directory) text wrapping
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }
}
