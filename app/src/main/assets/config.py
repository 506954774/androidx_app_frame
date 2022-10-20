

landmark_names = [
        'nose',
        'left_eye_inner', 'left_eye', 'left_eye_outer',
        'right_eye_inner', 'right_eye', 'right_eye_outer',
        'left_ear', 'right_ear',
        'mouth_left', 'mouth_right',
        'left_shoulder', 'right_shoulder',
        'left_elbow', 'right_elbow',
        'left_wrist', 'right_wrist',
        'left_pinky_1', 'right_pinky_1',
        'left_index_1', 'right_index_1',
        'left_thumb_2', 'right_thumb_2',
        'left_hip', 'right_hip',
        'left_knee', 'right_knee',
        'left_ankle', 'right_ankle',
        'left_heel', 'right_heel',
        'left_foot_index', 'right_foot_index',
    ]

push_up_config = dict(name="push_up",
                    first_class_name='up',
                    second_class_name='down',
                    pose_samples_folder="model/20220610/csv_1_push_up_out",
                    top_n_by_max_distance=10,
                    top_n_by_mean_distance=3,
                    plot_y_max=10,
                    first_enter_threshold=5,
                    second_enter_threshold=5,
                    rules = [['up','left_shoulder','left_hip','left_knee',">=",160,"身体不直"],
                    ['down','left_shoulder','left_elbow','left_wrist',"<=",110,"下压不足"]])

sit_up_config = dict(name="sit_up",
                    first_class_name='down',
                    second_class_name='up',
                    pose_samples_folder="model/20220610/csv_2_sit_up_out",
                    top_n_by_max_distance=8,
                    top_n_by_mean_distance=2,
                    plot_y_max=15,
                    first_enter_threshold=10,
                    second_enter_threshold=10,
                    rules = [['up','left_shoulder','left_hip','left_knee',"<=",40,"身体前倾不够"]])


siglebar_pullup_config = dict(name="siglebar_pullup",
                    first_class_name='down',
                    second_class_name='up',
                    pose_samples_folder="model/20220610/csv_3_singlebar_pullup_out",
                    top_n_by_max_distance=8,
                    top_n_by_mean_distance=3,
                    plot_y_max=10,
                    first_enter_threshold=5,
                    second_enter_threshold=5,
                    rules = [['up','left_shoulder','left_elbow','left_wrist',"<=",50,"上拉不到位"],
                            ['down','left_elbow','left_shoulder','left_hip',">=",150,"下放不直"]])

parallelbar_pullup_config = dict(name="parallelbar_pullup",
                    first_class_name='up',
                    second_class_name='down',
                    pose_samples_folder="model/20220610/csv_4_parallelbar_pullup_out",
                    top_n_by_max_distance=10,
                    top_n_by_mean_distance=3,
                    plot_y_max=10,
                    first_enter_threshold=5,
                    second_enter_threshold=5,
                    rules = [['up','left_shoulder','left_elbow','left_wrist',">=",150,"上撑不到位"],
                            ['down','left_shoulder','left_elbow','left_wrist',"<=",100,"下放不到位"]])


config_dict = dict(push_up_config=push_up_config,
                sit_up_config=sit_up_config,
                siglebar_pullup_config=siglebar_pullup_config,
                parallelbar_pullup_config=parallelbar_pullup_config)