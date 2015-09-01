# GalleryImageUploadSample
Sample Project to load images from gallery and upload them on to the server.

<b>CLIENT SIDE REQUIREMENTS:</b> 
- Update FILE_UPLOAD_URL (replace the ip with your server address) in Config.java


<b>SERVER SIDE REQUIREMENTS:</b>

- Create new folder name "AndroidFileUpload" inside www folder [ for WAMP ] or htdocs folder [ for XAMPP ].

- Inside this folder create <br/>
     a) fileUpload.php file & <br/>
     b) another folder name "uploads" 

<i>fileUpload.php</i> Contents: 

    <?php
    
     // Path to move uploaded files
      $target_path = "uploads/";
      
     // array for final json respone
      $response = array();
      
      // getting server ip address
      $server_ip = gethostbyname(gethostname());
      
      // final file url that is being uploaded
      $file_upload_url = 'http://' . $server_ip . '/' . 'AndroidFileUpload' . '/' . $target_path;
 
     if (isset($_FILES['image']['name'])) {
        $target_path = $target_path . basename($_FILES['image']['name']);
 
        $response['file_name'] = basename($_FILES['image']['name']);
 
        try {
            // Throws exception incase file is not being moved
            if (!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)) {
                // make error flag true
                $response['error'] = true;
                $response['message'] = 'Could not move the file!';
            }
 
             // File successfully uploaded
             $response['message'] = 'File uploaded successfully!';
             $response['error'] = false;
             $response['file_path'] = $file_upload_url . basename($_FILES['image']['name']);
        } catch (Exception $e) {
            // Exception occurred. Make error flag true
             $response['error'] = true;
             $response['message'] = $e->getMessage();
        }
       
     } else {
         // File parameter is missing
         $response['error'] = true;
         $response['message'] = 'Not received any file!!';
     }
 
    // Echo final json response to client
    echo json_encode($response);
    ?>


