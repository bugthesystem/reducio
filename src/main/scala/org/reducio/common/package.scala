package org.reducio

import java.util.concurrent._
import scala.util.DynamicVariable

package object common {

  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T]

    def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
      val right = task {
        taskB
      }
      val left = taskA
      (left, right.join())
    }

    def parallel[A, B, C](taskA: => A, taskB: => B, taskC: => C): (A, B, C) = {
      val tc = task {
        taskC
      }
      val tb = task {
        taskB
      }
      val ta = taskA
      (ta, tb.join(), tc.join())
    }
  }

  class DefaultTaskScheduler extends TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T] = {
      val t = new RecursiveTask[T] {
        def compute: T = body
      }
      Thread.currentThread match {
        case _: ForkJoinWorkerThread =>
          t.fork()
        case _ =>
          forkJoinPool.execute(t)
      }
      t
    }
  }

  val scheduler =
    new DynamicVariable[TaskScheduler](new DefaultTaskScheduler)

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.value.schedule(body)
  }

  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
    scheduler.value.parallel(taskA, taskB)
  }

  def parallel[A, B, C](taskA: => A, taskB: => B, taskC: => C): (A, B, C) = {
    scheduler.value.parallel(taskA, taskB, taskC)
  }
}
