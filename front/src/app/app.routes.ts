import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { unknownRouteGuard } from './auth/unknown-route.guard';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HomeComponent } from './home/home.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { PostCreateComponent } from './post/post-create/post-create.component';
import { PostDetailComponent } from './post/post-detail/post-detail.component';
import { PostsListComponent } from './post/posts-list/posts-list.component';
import { TopicsListComponent } from './topic/topics-list/topics-list.component';
import { ProfileComponent } from './user/profile/profile.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'posts', component: PostsListComponent, canActivate: [authGuard] },
  { path: 'posts/create', component: PostCreateComponent, canActivate: [authGuard] },
  { path: 'posts/:postId', component: PostDetailComponent, canActivate: [authGuard] },
  { path: 'topics', component: TopicsListComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: '**', component: NotFoundComponent, canActivate: [unknownRouteGuard] },
];
