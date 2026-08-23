export interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
  topic: string;
  createdAt: string;
}

export interface PostComment {
  author: string;
  content: string;
  createdAt: string;
}

export interface PostDetail extends Post {
  comments: PostComment[];
}
